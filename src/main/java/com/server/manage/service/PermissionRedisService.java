package com.server.manage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.server.manage.model.Permission;
import com.server.manage.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限Redis服务类
 * 处理权限相关数据在Redis中的存储和读取
 */
@Service
public class PermissionRedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 用户权限缓存key前缀
    private static final String USER_PERMISSIONS_PREFIX = "user:permissions:";
    
    // 角色权限缓存key前缀
    private static final String ROLE_PERMISSIONS_PREFIX = "role:permissions:";
    
    // 用户角色缓存key前缀
    private static final String USER_ROLES_PREFIX = "user:roles:";
    
    // 所有权限缓存key
    private static final String ALL_PERMISSIONS_KEY = "permissions:all";
    
    // 所有菜单缓存key
    private static final String ALL_MENUS_KEY = "menus:all";
    
    // 缓存过期时间（小时）
    private static final int CACHE_EXPIRE_HOURS = 2;

    /**
     * 缓存用户权限
     * @param userId 用户ID
     * @param permissions 权限码集合
     */
    public void cacheUserPermissions(Long userId, Set<String> permissions) {
        String key = USER_PERMISSIONS_PREFIX + userId;
        if (permissions != null && !permissions.isEmpty()) {
            redisTemplate.opsForSet().add(key, permissions.toArray(new String[0]));
            redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    /**
     * 获取用户权限
     * @param userId 用户ID
     * @return 权限码集合
     */
    public Set<String> getUserPermissions(Long userId) {
        String key = USER_PERMISSIONS_PREFIX + userId;
        Set<String> permissions = redisTemplate.opsForSet().members(key);
        return permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限码
     * @return 是否拥有权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        String key = USER_PERMISSIONS_PREFIX + userId;
        Boolean result = redisTemplate.opsForSet().isMember(key, permissionCode);
        return result != null && result;
    }

    /**
     * 缓存角色权限
     * @param roleId 角色ID
     * @param permissions 权限码集合
     */
    public void cacheRolePermissions(Long roleId, Set<String> permissions) {
        String key = ROLE_PERMISSIONS_PREFIX + roleId;
        if (permissions != null && !permissions.isEmpty()) {
            redisTemplate.opsForSet().add(key, permissions.toArray(new String[0]));
            redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    /**
     * 获取角色权限
     * @param roleId 角色ID
     * @return 权限码集合
     */
    public Set<String> getRolePermissions(Long roleId) {
        String key = ROLE_PERMISSIONS_PREFIX + roleId;
        Set<String> permissions = redisTemplate.opsForSet().members(key);
        return permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * 缓存用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     */
    public void cacheUserRoles(Long userId, Set<Long> roleIds) {
        String key = USER_ROLES_PREFIX + userId;
        // 将Long类型转换为String类型存储
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<String> stringRoleIds = roleIds.stream().map(String::valueOf).collect(Collectors.toSet());
            redisTemplate.opsForSet().add(key, stringRoleIds.toArray(new String[0]));
            redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    /**
     * 获取用户角色
     * @param userId 用户ID
     * @return 角色ID集合
     */
    public Set<Long> getUserRoles(Long userId) {
        String key = USER_ROLES_PREFIX + userId;
        Set<String> stringRoleIds = redisTemplate.opsForSet().members(key);
        // 将String类型转换为Long类型返回
        if (stringRoleIds == null) {
            return Collections.emptySet();
        }
        return stringRoleIds.stream().map(Long::valueOf).collect(Collectors.toSet());
    }

    /**
     * 缓存所有权限信息
     * @param permissions 权限列表
     */
    public void cacheAllPermissions(List<Permission> permissions) {
        String json = JsonUtil.toJson(permissions);
        if (json != null) {
            redisTemplate.opsForValue().set(ALL_PERMISSIONS_KEY, json, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    /**
     * 获取所有权限信息
     * @return 权限列表
     */
    public List<Permission> getAllPermissions() {
        String json = redisTemplate.opsForValue().get(ALL_PERMISSIONS_KEY);
        if (json != null) {
            return JsonUtil.fromJson(json, new TypeReference<List<Permission>>() {});
        }
        return null;
    }

    /**
     * 缓存所有菜单信息
     * @param menus 菜单列表
     */
    public void cacheAllMenus(List<com.server.manage.model.Menu> menus) {
        String json = JsonUtil.toJson(menus);
        if (json != null) {
            redisTemplate.opsForValue().set(ALL_MENUS_KEY, json, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    /**
     * 获取所有菜单信息
     * @return 菜单列表
     */
    public List<com.server.manage.model.Menu> getAllMenus() {
        String json = redisTemplate.opsForValue().get(ALL_MENUS_KEY);
        if (json != null) {
            return JsonUtil.fromJson(json, new TypeReference<List<com.server.manage.model.Menu>>() {});
        }
        return null;
    }

    /**
     * 删除用户权限缓存
     * @param userId 用户ID
     */
    public void removeUserPermissionsCache(Long userId) {
        String key = USER_PERMISSIONS_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 删除角色权限缓存
     * @param roleId 角色ID
     */
    public void removeRolePermissionsCache(Long roleId) {
        String key = ROLE_PERMISSIONS_PREFIX + roleId;
        redisTemplate.delete(key);
    }

    /**
     * 删除用户角色缓存
     * @param userId 用户ID
     */
    public void removeUserRolesCache(Long userId) {
        String key = USER_ROLES_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 清空所有权限相关缓存
     */
    public void clearAllPermissionCache() {
        // 删除所有用户权限缓存
        Set<String> userPermissionKeys = redisTemplate.keys(USER_PERMISSIONS_PREFIX + "*");
        if (userPermissionKeys != null && !userPermissionKeys.isEmpty()) {
            redisTemplate.delete(userPermissionKeys);
        }

        // 删除所有角色权限缓存
        Set<String> rolePermissionKeys = redisTemplate.keys(ROLE_PERMISSIONS_PREFIX + "*");
        if (rolePermissionKeys != null && !rolePermissionKeys.isEmpty()) {
            redisTemplate.delete(rolePermissionKeys);
        }

        // 删除所有用户角色缓存
        Set<String> userRoleKeys = redisTemplate.keys(USER_ROLES_PREFIX + "*");
        if (userRoleKeys != null && !userRoleKeys.isEmpty()) {
            redisTemplate.delete(userRoleKeys);
        }

        // 删除所有权限信息缓存
        redisTemplate.delete(ALL_PERMISSIONS_KEY);
        
        // 删除所有菜单信息缓存
        redisTemplate.delete(ALL_MENUS_KEY);
    }
}