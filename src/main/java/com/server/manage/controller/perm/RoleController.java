package com.server.manage.controller.perm;

import com.server.manage.common.ApiResponse;
import com.server.manage.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色相关接口（占位）
 */
@RestController
@RequestMapping("/api/permissions/roles")
public class RoleController {

    @Autowired
    private com.server.manage.service.IRoleService roleService;

    @GetMapping("/list")
    public ApiResponse<List<Role>> listRoles() {
        return ApiResponse.ok(null);
    }

    @PostMapping("/create")
    public ApiResponse<String> createRole(@RequestBody Role role) {
    roleService.create(role);
        return ApiResponse.ok("角色创建成功");
    }

    @PutMapping("/update")
    public ApiResponse<String> updateRole(@RequestBody Role role) {
    roleService.update(role);
        return ApiResponse.ok("角色更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteRole(@PathVariable Long id) {
    roleService.delete(id);
        return ApiResponse.ok("角色删除成功");
    }

    @GetMapping("/{roleId}/permissions")
    public ApiResponse<Set<String>> getRolePermissions(@PathVariable Long roleId) {
    Set<String> permissions = roleService.getRolePermissions(roleId);
        return ApiResponse.ok(permissions);
    }

    @PostMapping("/{roleId}/permissions")
    public ApiResponse<String> assignRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
    roleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.ok("角色权限分配成功");
    }
}
