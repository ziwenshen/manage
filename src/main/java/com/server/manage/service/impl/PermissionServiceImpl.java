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
        // update cache
        permissionRedisService.cacheAllPermissions(permissions);
        return permissions;
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
