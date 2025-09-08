package com.server.manage.service.impl;

import com.server.manage.mapper.MenuMapper;
import com.server.manage.model.Menu;
import com.server.manage.service.IMenuService;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    public Long create(Menu menu) {
        menuMapper.insert(menu);
        permissionRedisService.clearAllPermissionCache();
        return menu.getId();
    }

    @Override
    @Transactional
    public void update(Menu menu) {
        menuMapper.update(menu);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        menuMapper.deleteById(id);
        permissionRedisService.clearAllPermissionCache();
    }
}
