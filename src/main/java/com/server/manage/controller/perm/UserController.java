package com.server.manage.controller.perm;

import com.server.manage.common.ApiResponse;
import com.server.manage.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 用户相关接口（用户角色分配、用户权限查询）
 */
@RestController
@RequestMapping("/api/permissions/users")
public class UserController {

    @Autowired
    private com.server.manage.service.IUserService userService;

    @Autowired
    private IPermissionService permissionService;

    @GetMapping("/{userId}/roles")
    public ApiResponse<Set<Long>> getUserRoles(@PathVariable Long userId) {
    Set<Long> roles = userService.getUserRoles(userId);
        return ApiResponse.ok(roles);
    }

    @PostMapping("/{userId}/roles")
    public ApiResponse<String> assignUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
    userService.assignUserRoles(userId, roleIds);
        return ApiResponse.ok("用户角色分配成功");
    }

    @GetMapping("/{userId}/permissions")
    public ApiResponse<Set<String>> getUserPermissions(@PathVariable Long userId) {
        Set<String> permissions = permissionService.loadUserPermissions(userId);
        return ApiResponse.ok(permissions);
    }
}
