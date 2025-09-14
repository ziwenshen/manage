package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.dto.common.PageResult;
import com.server.manage.dto.user.*;
import com.server.manage.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 创建用户
     */
    @HasPermission(value = "menu:user:add", description = "创建用户")
    @PostMapping("/add")
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return ApiResponse.ok(user);
    }

    /**
     * 根据ID获取用户
     */
    @HasPermission(value = "menu:user:view", description = "查看用户详情")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ApiResponse.ok(user);
    }

    /**
     * 根据用户名获取用户
     */
    @HasPermission(value = "menu:user:view", description = "查看用户信息")
    @GetMapping("/getUser/{username}")
    public ApiResponse<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ApiResponse.ok(user);
    }

    /**
     * 更新用户
     */
    @HasPermission(value = "menu:user:edit", description = "编辑用户信息")
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id,
                                              @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ApiResponse.ok(user);
    }

    /**
     * 删除用户
     */
    @HasPermission(value = "menu:user:delete", description = "删除用户")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.ok("用户删除成功");
    }

    /**
     * 分页查询用户列表
     */
    @HasPermission(value = "menu:user:view", description = "查看用户列表")
    @GetMapping
    public ApiResponse<PageResult<UserResponse>> getUserList(UserQueryRequest request) {
        List<UserResponse> users = userService.getUserList(request);
        Long total = userService.getUserCount(request);

        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;

        PageResult<UserResponse> result = PageResult.of(users, total, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 检查用户名是否存在
     */
    @HasPermission(value = "menu:user:view", description = "检查用户名是否存在")
    @GetMapping("/exists/{username}")
    public ApiResponse<Boolean> existsByUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.ok(exists);
    }

    /**
     * 获取用户的角色列表
     */
    @HasPermission(value = "menu:user:view", description = "查看用户角色")
    @GetMapping("/{userId}/roles")
    public ApiResponse<Set<Long>> getUserRoles(@PathVariable Long userId) {
        Set<Long> roleIds = userService.getUserRoles(userId);
        return ApiResponse.ok(roleIds);
    }

    /**
     * 分配用户角色
     */
    @HasPermission(value = "menu:user:assignrole", description = "分配用户角色")
    @PostMapping("/{userId}/roles")
    public ApiResponse<String> assignUserRoles(@PathVariable Long userId, @RequestBody UserRoleAssignRequest request) {
        userService.assignUserRoles(userId, request);
        return ApiResponse.ok("用户角色分配成功");
    }
}
