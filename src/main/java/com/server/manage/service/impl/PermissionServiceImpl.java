package com.server.manage.service.impl;

import com.server.manage.mapper.PermissionMapper;
import com.server.manage.model.Permission;
import com.server.manage.service.IPermissionService;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        // 优先从缓存读取
        Set<String> cached = permissionRedisService.getUserPermissions(userId);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        // 从数据库聚合用户权限：先查询用户直赋权限，再查询用户角色关联的权限
        Set<String> permissions = new HashSet<>();

        // 1) 用户直赋权限
        Set<Long> userPermissionIds = userPermissionMapper.selectPermissionIdsByUserId(userId);
        if (userPermissionIds != null && !userPermissionIds.isEmpty()) {
            for (Long pid : userPermissionIds) {
                Permission p = permissionMapper.selectById(pid);
                if (p != null && p.getCode() != null) {
                    permissions.add(p.getCode());
                }
            }
        }

        // 2) 通过用户角色获取权限
        Set<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Long> permIds = rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds);
            if (permIds != null && !permIds.isEmpty()) {
                for (Long pid : permIds) {
                    Permission p = permissionMapper.selectById(pid);
                    if (p != null && p.getCode() != null) {
                        permissions.add(p.getCode());
                    }
                }
            }
        }
        // 缓存结果
        permissionRedisService.cacheUserPermissions(userId, permissions);
        return permissions;
    }

    @Override
    public void refreshUserPermissions(Long userId) {
        // 重新加载并刷新缓存（占位实现）
        permissionRedisService.removeUserPermissionsCache(userId);
        loadUserPermissions(userId);
    }
}
