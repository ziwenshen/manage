package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.dto.common.PageResult;
import com.server.manage.dto.role.RoleQueryRequest;
import com.server.manage.dto.role.RoleResponse;
import com.server.manage.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色相关接口（占位）
 */
@RestController
@RequestMapping("/permissions/roles")
public class RoleController {

    @Autowired
    private com.server.manage.service.IRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @HasPermission(value = "menu:role:view", description = "查看角色列表")
    @GetMapping("/list")
    public ApiResponse<PageResult<RoleResponse>> getRoleList(RoleQueryRequest request) {
        List<RoleResponse> roles = roleService.getRoleList(request);
        Long total = roleService.getRoleCount(request);

        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;

        PageResult<RoleResponse> result = PageResult.of(roles, total, page, size);
        return ApiResponse.ok(result);
    }

    @HasPermission(value = "menu:role:add", description = "创建角色")
    @PostMapping("/create")
    public ApiResponse<String> createRole(@RequestBody Role role) {
    roleService.create(role);
        return ApiResponse.ok("角色创建成功");
    }

    @HasPermission(value = "menu:role:edit", description = "编辑角色")
    @PutMapping("/update")
    public ApiResponse<String> updateRole(@RequestBody Role role) {
    roleService.update(role);
        return ApiResponse.ok("角色更新成功");
    }

    @HasPermission(value = "menu:role:delete", description = "删除角色")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteRole(@PathVariable Long id) {
    roleService.delete(id);
        return ApiResponse.ok("角色删除成功");
    }

    @HasPermission(value = "menu:role:viewpermission", description = "查看角色权限")
    @GetMapping("/{roleId}/permissions")
    public ApiResponse<Set<String>> getRolePermissions(@PathVariable Long roleId) {
    Set<String> permissions = roleService.getRolePermissions(roleId);
        return ApiResponse.ok(permissions);
    }

    @HasPermission(value = "menu:role:assignpermission", description = "分配角色权限")
    @PostMapping("/{roleId}/permissions")
    public ApiResponse<String> assignRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
    roleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.ok("角色权限分配成功");
    }
}
