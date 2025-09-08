package com.server.manage.service.impl;

import com.server.manage.mapper.RoleMapper;
import com.server.manage.model.Role;
import com.server.manage.service.IRoleService;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionRedisService permissionRedisService;

    @Autowired
    private com.server.manage.mapper.RolePermissionWriteMapper rolePermissionWriteMapper;

    @Autowired
    private com.server.manage.service.IPermissionService permissionService;
    @Autowired
    private com.server.manage.mapper.UserRoleMapper userRoleMapper;
    @Autowired
    private com.server.manage.mapper.RolePermissionMapper rolePermissionMapper;

    @Autowired
    private com.server.manage.mapper.PermissionMapper permissionMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectAll();
    }

    @Override
    @Transactional
    public Long create(Role role) {
        roleMapper.insert(role);
        permissionRedisService.clearAllPermissionCache();
        return role.getId();
    }

    @Override
    @Transactional
    public void update(Role role) {
        roleMapper.update(role);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.deleteById(id);
        permissionRedisService.clearAllPermissionCache();
    }

    @Override
    public Set<String> getRolePermissions(Long roleId) {
        // 1. 构建 roleId 集合并查询 permissionId 集合
        java.util.Set<Long> roleIds = new java.util.HashSet<>();
        roleIds.add(roleId);
        java.util.Set<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds);
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        // 2. 批量查询 permission 实体并收集 code
        java.util.List<com.server.manage.model.Permission> perms = permissionMapper.selectByIds(permissionIds);
        Set<String> codes = new HashSet<>();
        if (perms != null && !perms.isEmpty()) {
            for (com.server.manage.model.Permission p : perms) {
                if (p != null && p.getCode() != null) {
                    codes.add(p.getCode());
                }
            }
        }
        return codes;
    }

    @Override
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 1. 删除原有关联
        rolePermissionWriteMapper.deleteByRoleId(roleId);
        // 2. 批量插入新关联（如果有）
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionWriteMapper.batchInsert(roleId, permissionIds);
        }
        // 3. 清理缓存
        permissionRedisService.removeRolePermissionsCache(roleId);
        // 4. 刷新该角色下所有用户的权限缓存
    Set<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long uid : userIds) {
                try {
                    permissionService.refreshUserPermissions(uid);
                } catch (Exception ignored) {
                    // 刷新失败不影响主事务，记录或延后处理可在未来实现
                }
            }
        }
    }
}
