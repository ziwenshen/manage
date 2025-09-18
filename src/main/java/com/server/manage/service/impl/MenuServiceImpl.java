package com.server.manage.service.impl;

import com.server.manage.dto.menu.MenuCreateRequest;
import com.server.manage.dto.menu.MenuQueryRequest;
import com.server.manage.dto.menu.MenuResponse;
import com.server.manage.dto.menu.MenuUpdateRequest;
import com.server.manage.mapper.MenuMapper;
import com.server.manage.model.Menu;
import com.server.manage.service.IMenuService;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private PermissionRedisService permissionRedisService;

    @Override
    public List<Menu> listAll() {
        List<Menu> menus = menuMapper.selectAll();
        permissionRedisService.cacheAllMenus(menus);
        return menus;
    }

    @Override
    public Menu getById(Long id) {
        if (id == null) {
            return null;
        }
        return menuMapper.selectById(id);
    }

    @Override
    public Menu getByMenuCode(String menuCode) {
        if (menuCode == null || menuCode.trim().isEmpty()) {
            return null;
        }
        return menuMapper.selectByMenuCode(menuCode);
    }

    @Override
    public List<Menu> listByParentId(Long parentId) {
        return menuMapper.selectByParentId(parentId);
    }

    @Override
    public List<Menu> listByModule(String module) {
        if (module == null || module.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return menuMapper.selectByModule(module);
    }

    @Override
    public List<MenuResponse> getMenuTree() {
        List<Menu> allMenus = menuMapper.selectAll();
        return buildMenuResponseTree(allMenus, null);
    }

    @Override
    @Transactional
    public Long create(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("菜单对象不能为空");
        }
        
        // 设置默认值
        if (menu.getNodeType() == null) {
            menu.setNodeType(2); // 默认为页面
        }
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        
        menuMapper.insert(menu);
        permissionRedisService.clearAllPermissionCache();
        return menu.getId();
    }

    @Override
    @Transactional
    public void update(Menu menu) {
        if (menu == null || menu.getId() == null) {
            throw new IllegalArgumentException("菜单对象或ID不能为空");
        }
        
        menuMapper.update(menu);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("菜单ID不能为空");
        }
        
        // 先删除子菜单
        menuMapper.deleteByParentId(id);
        // 再删除当前菜单
        menuMapper.deleteById(id);
        
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 构建菜单响应树
     */
    private List<MenuResponse> buildMenuResponseTree(List<Menu> allMenus, Long parentId) {
        List<MenuResponse> result = new ArrayList<>();

        for (Menu menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                MenuResponse menuResponse = convertToMenuResponse(menu);
                // 递归构建子菜单
                List<MenuResponse> children = buildMenuResponseTree(allMenus, menu.getId());
                menuResponse.setChildren(children);
                result.add(menuResponse);
            }
        }

        return result;
    }

    @Override
    public List<MenuResponse> getMenuList(MenuQueryRequest request) {
        if (request == null) {
            request = new MenuQueryRequest();
        }

        // 计算分页参数
        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;
        Integer offset = (page - 1) * size;

        // 查询菜单列表
        List<Menu> menus = menuMapper.selectPageList(request, offset, size);

        // 转换为响应DTO
        return menus.stream()
                .map(this::convertToMenuResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getMenuCount(MenuQueryRequest request) {
        if (request == null) {
            request = new MenuQueryRequest();
        }
        return menuMapper.selectCount(request);
    }

    /**
     * 转换Menu实体为MenuResponse
     */
    private MenuResponse convertToMenuResponse(Menu menu) {
        if (menu == null) {
            return null;
        }

        MenuResponse response = new MenuResponse();
        BeanUtils.copyProperties(menu, response);
        return response;
    }

    @Override
    @Transactional
    public Long createMenu(MenuCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("菜单创建请求不能为空");
        }

        // 检查菜单编码是否已存在
        if (request.getMenuCode() != null && getByMenuCode(request.getMenuCode()) != null) {
            throw new IllegalArgumentException("菜单编码已存在: " + request.getMenuCode());
        }

        Menu menu = new Menu();
        BeanUtils.copyProperties(request, menu);
        menu.setCreatedAt(java.time.LocalDateTime.now());

        Long menuId = create(menu);

        // 清除相关缓存
        permissionRedisService.clearAllPermissionCache();

        return menuId;
    }

    @Override
    @Transactional
    public void updateMenu(MenuUpdateRequest request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("菜单更新请求或ID不能为空");
        }

        // 检查菜单是否存在
        Menu existingMenu = getById(request.getId());
        if (existingMenu == null) {
            throw new IllegalArgumentException("菜单不存在: " + request.getId());
        }

        // 检查菜单编码是否与其他菜单冲突
        if (request.getMenuCode() != null && !request.getMenuCode().equals(existingMenu.getMenuCode())) {
            Menu conflictMenu = getByMenuCode(request.getMenuCode());
            if (conflictMenu != null && !conflictMenu.getId().equals(request.getId())) {
                throw new IllegalArgumentException("菜单编码已存在: " + request.getMenuCode());
            }
        }

        Menu menu = new Menu();
        BeanUtils.copyProperties(request, menu);

        update(menu);

        // 清除相关缓存
        permissionRedisService.clearAllPermissionCache();
    }
}
