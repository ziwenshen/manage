package com.server.manage.service.impl;

import com.server.manage.dto.menu.MenuPermissionTreeResponse;
import com.server.manage.dto.permission.PermissionQueryRequest;
import com.server.manage.dto.permission.PermissionResponse;
import com.server.manage.dto.permission.PermissionTreeResponse;
import com.server.manage.mapper.PermissionMapper;
import com.server.manage.model.Menu;
import com.server.manage.model.Permission;
import com.server.manage.service.IMenuService;
import com.server.manage.service.IPermissionService;
import com.server.manage.service.PermissionRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionRedisService permissionRedisService;

    @Autowired
    private com.server.manage.mapper.UserRoleMapper userRoleMapper;

    @Autowired
    private com.server.manage.mapper.RolePermissionMapper rolePermissionMapper;

    @Autowired
    private com.server.manage.mapper.UserPermissionMapper userPermissionMapper;

    @Autowired
    private IMenuService menuService;

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public List<Permission> listAll() {
        List<Permission> permissions = permissionMapper.selectAll();
        return permissions;
    }

    /**
     * 查询用户有view权限的菜单（文件夹或界面），并组装为树结构
     */
    @Override
    public List<com.server.manage.dto.menu.MenuResponse> getUserMenusWithViewPermission(Long userId) {
        List<java.util.Map<String, Object>> menuData = permissionMapper.selectUserMenusWithViewPermission(userId);
        // 转换为MenuResponse对象
        java.util.Map<Long, com.server.manage.dto.menu.MenuResponse> menuMap = new java.util.HashMap<>();
        for (java.util.Map<String, Object> data : menuData) {
            Long id = ((Number)data.get("id")).longValue();
            com.server.manage.dto.menu.MenuResponse menu = new com.server.manage.dto.menu.MenuResponse();
            menu.setId(id);
            menu.setMenuCode((String)data.get("menu_code"));
            menu.setName((String)data.get("name"));
            menu.setModule((String)data.get("module"));
            menu.setNodeType(data.get("node_type") == null ? null : ((Number)data.get("node_type")).intValue());
            menu.setSortOrder(data.get("sort_order") == null ? null : ((Number)data.get("sort_order")).intValue());
            Object createdAtObj = data.get("created_at");
            if (createdAtObj instanceof java.sql.Timestamp) {
                menu.setCreatedAt(((java.sql.Timestamp) createdAtObj).toLocalDateTime());
            } else if (createdAtObj instanceof java.time.LocalDateTime) {
                menu.setCreatedAt((java.time.LocalDateTime) createdAtObj);
            } else {
                menu.setCreatedAt(null);
            }
            menu.setParentId(data.get("parent_id") == null ? null : ((Number)data.get("parent_id")).longValue());
            menu.setChildren(new java.util.ArrayList<>());
            menuMap.put(id, menu);
        }
        // 构建树结构
        List<com.server.manage.dto.menu.MenuResponse> roots = new java.util.ArrayList<>();
        for (com.server.manage.dto.menu.MenuResponse menu : menuMap.values()) {
            if (menu.getParentId() == null || !menuMap.containsKey(menu.getParentId())) {
                roots.add(menu);
            } else {
                menuMap.get(menu.getParentId()).getChildren().add(menu);
            }
        }
        return roots;
    }

    @Override
    @Transactional
    public Long create(Permission permission) {
        permissionMapper.insert(permission);
        // 清理缓存
        permissionRedisService.clearAllPermissionCache();
        return permission.getId();
    }

    @Override
    @Transactional
    public void update(Permission permission) {
        permissionMapper.update(permission);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        permissionMapper.deleteById(id);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    public Set<String> loadUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        log.debug("加载用户权限: userId={}", userId);
        Set<String> permissions = new HashSet<>();

        try {
            // 1) 获取用户直赋权限ID
            Set<Long> userPermissionIds = userPermissionMapper.selectPermissionIdsByUserId(userId);

            // 2) 获取用户角色关联的权限ID
            Set<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            Set<Long> rolePermissionIds = Collections.emptySet();
            if (roleIds != null && !roleIds.isEmpty()) {
                rolePermissionIds = rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds);
            }

            // 3) 合并所有权限ID
            Set<Long> allPermissionIds = new HashSet<>();
            if (userPermissionIds != null) {
                allPermissionIds.addAll(userPermissionIds);
            }
            if (rolePermissionIds != null) {
                allPermissionIds.addAll(rolePermissionIds);
            }

            // 4) 批量查询权限信息，避免N+1查询问题
            if (!allPermissionIds.isEmpty()) {
                List<Permission> permissionList = permissionMapper.selectByIds(allPermissionIds);
                for (Permission permission : permissionList) {
                    if (permission != null && permission.getCode() != null && !permission.getCode().trim().isEmpty()) {
                        permissions.add(permission.getCode());
                    }
                }
            }

            log.debug("用户 {} 拥有权限: {}", userId, permissions);
            return permissions;

        } catch (Exception e) {
            log.error("加载用户权限失败: userId={}, error={}", userId, e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    @Override
    public void refreshUserPermissions(Long userId) {
        // 重新加载并刷新缓存（占位实现）
        permissionRedisService.removeUserPermissionsCache(userId);
        loadUserPermissions(userId);
    }

    @Override
    public List<MenuPermissionTreeResponse> getMenuPermissionTree() {
        try {
            // 1. 获取所有菜单
            List<Menu> allMenus = menuService.listAll();

            // 2. 获取所有权限
            List<Permission> allPermissions = permissionMapper.selectAll();

            // 3. 按菜单编码分组权限
            Map<String, List<Permission>> permissionsByMenuCode = allPermissions.stream()
                    .filter(p -> p.getMenuCode() != null)
                    .collect(Collectors.groupingBy(Permission::getMenuCode));

            // 4. 转换菜单为响应对象
            Map<Long, MenuPermissionTreeResponse> menuMap = new HashMap<>();
            for (Menu menu : allMenus) {
                MenuPermissionTreeResponse response = convertToMenuPermissionResponse(menu);

                // 添加该菜单下的权限
                List<Permission> menuPermissions = permissionsByMenuCode.get(menu.getMenuCode());
                if (menuPermissions != null) {
                    for (Permission permission : menuPermissions) {
                        MenuPermissionTreeResponse.PermissionInfo permInfo = new MenuPermissionTreeResponse.PermissionInfo();
                        BeanUtils.copyProperties(permission, permInfo);
                        response.getPermissions().add(permInfo);
                    }
                }

                menuMap.put(menu.getId(), response);
            }

            // 5. 构建树结构
            List<MenuPermissionTreeResponse> roots = new ArrayList<>();
            for (MenuPermissionTreeResponse menu : menuMap.values()) {
                if (menu.getParentId() == null || !menuMap.containsKey(menu.getParentId())) {
                    roots.add(menu);
                } else {
                    menuMap.get(menu.getParentId()).getChildren().add(menu);
                }
            }

            // 6. 按排序字段排序
            sortMenuTree(roots);

            return roots;

        } catch (Exception e) {
            log.error("获取菜单权限树失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 转换Menu为MenuPermissionTreeResponse
     */
    private MenuPermissionTreeResponse convertToMenuPermissionResponse(Menu menu) {
        MenuPermissionTreeResponse response = new MenuPermissionTreeResponse();
        BeanUtils.copyProperties(menu, response);
        return response;
    }

    /**
     * 递归排序菜单树
     */
    private void sortMenuTree(List<MenuPermissionTreeResponse> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }

        // 按sortOrder排序，如果sortOrder相同则按id排序
        menus.sort((m1, m2) -> {
            int sortCompare = Integer.compare(
                m1.getSortOrder() != null ? m1.getSortOrder() : 0,
                m2.getSortOrder() != null ? m2.getSortOrder() : 0
            );
            if (sortCompare != 0) {
                return sortCompare;
            }
            return Long.compare(m1.getId(), m2.getId());
        });

        // 递归排序子菜单
        for (MenuPermissionTreeResponse menu : menus) {
            sortMenuTree(menu.getChildren());
        }
    }

    @Override
    public List<PermissionResponse> getPermissionList(PermissionQueryRequest request) {
        if (request == null) {
            request = new PermissionQueryRequest();
        }

        // 计算分页参数
        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;
        Integer offset = (page - 1) * size;

        // 查询权限列表
        List<Permission> permissions = permissionMapper.selectPageList(request, offset, size);

        // 转换为响应DTO
        return permissions.stream()
                .map(this::convertToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getPermissionCount(PermissionQueryRequest request) {
        if (request == null) {
            request = new PermissionQueryRequest();
        }
        return permissionMapper.selectCount(request);
    }

    @Override
    public List<PermissionTreeResponse> getPermissionTree() {
        // 获取所有菜单和权限
        List<Menu> allMenus = menuService.listAll();
        List<Permission> allPermissions = permissionMapper.selectAll();

        // 构建权限树
        return buildPermissionTree(allMenus, allPermissions, null);
    }

    @Override
    public Permission getByCode(String code) {
        return permissionMapper.selectByCode(code);
    }

    /**
     * 转换Permission实体为PermissionResponse
     */
    private PermissionResponse convertToPermissionResponse(Permission permission) {
        if (permission == null) {
            return null;
        }

        PermissionResponse response = new PermissionResponse();
        BeanUtils.copyProperties(permission, response);
        return response;
    }

    /**
     * 构建权限树形结构
     */
    private List<PermissionTreeResponse> buildPermissionTree(List<Menu> allMenus, List<Permission> allPermissions, Long parentId) {
        List<PermissionTreeResponse> result = new ArrayList<>();

        // 添加当前层级的菜单节点
        for (Menu menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                PermissionTreeResponse menuNode = PermissionTreeResponse.createMenuNode(
                    menu.getId(), menu.getName(), menu.getMenuCode(),
                    menu.getModule(), menu.getSortOrder(), menu.getCreatedAt()
                );

                // 递归添加子菜单
                List<PermissionTreeResponse> children = buildPermissionTree(allMenus, allPermissions, menu.getId());

                // 添加当前菜单的权限
                List<PermissionTreeResponse> permissions = allPermissions.stream()
                    .filter(p -> menu.getMenuCode().equals(p.getMenuCode()))
                    .map(p -> PermissionTreeResponse.createPermissionNode(
                        p.getId(), p.getName(), p.getCode(), p.getModule(),
                        p.getMenuCode(), p.getAction(), p.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

                children.addAll(permissions);
                menuNode.setChildren(children);
                result.add(menuNode);
            }
        }

        return result;
    }
}
