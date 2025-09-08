package com.server.manage.service.impl;

import com.server.manage.mapper.UserRoleMapper;
import com.server.manage.service.IUserService;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionRedisService permissionRedisService;

    @Autowired
    private com.server.manage.mapper.UserRoleWriteMapper userRoleWriteMapper;

    @Override
    public Set<Long> getUserRoles(Long userId) {
        Set<Long> roles = userRoleMapper.selectRoleIdsByUserId(userId);
        return roles == null ? new HashSet<>() : roles;
    }

    @Override
    @Transactional
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        // 删除原用户角色
        userRoleWriteMapper.deleteByUserId(userId);
        // 批量插入新角色（如果有）
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleWriteMapper.batchInsert(userId, roleIds);
        }
        // 清理相关缓存
        permissionRedisService.removeUserRolesCache(userId);
        permissionRedisService.removeUserPermissionsCache(userId);
    }
}
