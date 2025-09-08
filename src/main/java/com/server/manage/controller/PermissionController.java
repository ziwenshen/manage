package com.server.manage.controller;

import com.server.manage.common.ApiResponse;
import com.server.manage.model.*;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 * 处理用户、角色、权限和菜单的管理操作
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionRedisService permissionRedisService;

    // ==================== 权限管理 ====================

    /**
     * 获取所有权限列表
     * @return 权限列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Permission>> listPermissions() {
        List<Permission> permissions = permissionRedisService.getAllPermissions();
        return ApiResponse.ok(permissions);
    }

    /**
     * 创建权限
     * @param permission 权限信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<String> createPermission(@RequestBody Permission permission) {
        // 这里应该调用服务层进行数据库操作
        // 创建成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("权限创建成功");
    }

    /**
     * 更新权限
     * @param permission 权限信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public ApiResponse<String> updatePermission(@RequestBody Permission permission) {
        // 这里应该调用服务层进行数据库操作
        // 更新成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("权限更新成功");
    }

    /**
     * 删除权限
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        // 这里应该调用服务层进行数据库操作
        // 删除成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("权限删除成功");
    }

    // ==================== 角色管理 ====================

    /**
     * 获取所有角色列表
     * @return 角色列表
     */
    @GetMapping("/roles/list")
    public ApiResponse<List<Role>> listRoles() {
        // 应该从数据库获取角色列表
        return ApiResponse.ok(null);
    }

    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建结果
     */
    @PostMapping("/roles/create")
    public ApiResponse<String> createRole(@RequestBody Role role) {
        // 这里应该调用服务层进行数据库操作
        // 创建成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("角色创建成功");
    }

    /**
     * 更新角色
     * @param role 角色信息
     * @return 更新结果
     */
    @PutMapping("/roles/update")
    public ApiResponse<String> updateRole(@RequestBody Role role) {
        // 这里应该调用服务层进行数据库操作
        // 更新成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("角色更新成功");
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/roles/delete/{id}")
    public ApiResponse<String> deleteRole(@PathVariable Long id) {
        // 这里应该调用服务层进行数据库操作
        // 删除成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("角色删除成功");
    }

    /**
     * 获取角色的权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/roles/{roleId}/permissions")
    public ApiResponse<Set<String>> getRolePermissions(@PathVariable Long roleId) {
        Set<String> permissions = permissionRedisService.getRolePermissions(roleId);
        return ApiResponse.ok(permissions);
    }

    /**
     * 分配角色权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 分配结果
     */
    @PostMapping("/roles/{roleId}/permissions")
    public ApiResponse<String> assignRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        // 这里应该调用服务层进行数据库操作
        // 分配成功后需要更新相关缓存
        permissionRedisService.removeRolePermissionsCache(roleId);
        return ApiResponse.ok("角色权限分配成功");
    }

    // ==================== 用户管理 ====================

    /**
     * 获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/users/{userId}/roles")
    public ApiResponse<Set<Long>> getUserRoles(@PathVariable Long userId) {
        Set<Long> roles = permissionRedisService.getUserRoles(userId);
        return ApiResponse.ok(roles);
    }

    /**
     * 分配用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 分配结果
     */
    @PostMapping("/users/{userId}/roles")
    public ApiResponse<String> assignUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        // 这里应该调用服务层进行数据库操作
        // 分配成功后需要更新相关缓存
        permissionRedisService.removeUserRolesCache(userId);
        permissionRedisService.removeUserPermissionsCache(userId);
        return ApiResponse.ok("用户角色分配成功");
    }

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/users/{userId}/permissions")
    public ApiResponse<Set<String>> getUserPermissions(@PathVariable Long userId) {
        Set<String> permissions = permissionRedisService.getUserPermissions(userId);
        return ApiResponse.ok(permissions);
    }

    // ==================== 菜单管理 ====================

    /**
     * 获取所有菜单列表
     * @return 菜单列表
     */
    @GetMapping("/menus/list")
    public ApiResponse<List<Menu>> listMenus() {
        List<Menu> menus = permissionRedisService.getAllMenus();
        return ApiResponse.ok(menus);
    }

    /**
     * 创建菜单
     * @param menu 菜单信息
     * @return 创建结果
     */
    @PostMapping("/menus/create")
    public ApiResponse<String> createMenu(@RequestBody Menu menu) {
        // 这里应该调用服务层进行数据库操作
        // 创建成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("菜单创建成功");
    }

    /**
     * 更新菜单
     * @param menu 菜单信息
     * @return 更新结果
     */
    @PutMapping("/menus/update")
    public ApiResponse<String> updateMenu(@RequestBody Menu menu) {
        // 这里应该调用服务层进行数据库操作
        // 更新成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("菜单更新成功");
    }

    /**
     * 删除菜单
     * @param id 菜单ID
     * @return 删除结果
     */
    @DeleteMapping("/menus/delete/{id}")
    public ApiResponse<String> deleteMenu(@PathVariable Long id) {
        // 这里应该调用服务层进行数据库操作
        // 删除成功后需要清除相关缓存
        permissionRedisService.clearAllPermissionCache();
        return ApiResponse.ok("菜单删除成功");
    }
}