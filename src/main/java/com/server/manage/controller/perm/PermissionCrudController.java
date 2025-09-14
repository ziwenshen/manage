package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.dto.menu.MenuPermissionTreeResponse;
import com.server.manage.model.Permission;
import com.server.manage.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限（permission）相关的 CRUD 接口
 */
@RestController
@RequestMapping("/permissions/perm")
public class PermissionCrudController {

    @Autowired
    private IPermissionService permissionService;

    @HasPermission(value = "menu:permission:view", description = "查看权限列表")
    @GetMapping("/list")
    public ApiResponse<List<Permission>> listPermissions() {
        List<Permission> permissions = permissionService.listAll();
        return ApiResponse.ok(permissions);
    }

    @HasPermission(value = "menu:permission:add", description = "创建权限")
    @PostMapping("/create")
    public ApiResponse<String> createPermission(@RequestBody Permission permission) {
        permissionService.create(permission);
        return ApiResponse.ok("权限创建成功");
    }

    @HasPermission(value = "menu:permission:edit", description = "编辑权限")
    @PutMapping("/update")
    public ApiResponse<String> updatePermission(@RequestBody Permission permission) {
        permissionService.update(permission);
        return ApiResponse.ok("权限更新成功");
    }

    @HasPermission(value = "menu:permission:delete", description = "删除权限")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.ok("权限删除成功");
    }

    /**
     * 获取菜单权限树，用于角色分配权限选择
     */
    @HasPermission(value = "menu:permission:view", description = "查看菜单权限树")
    @GetMapping("/menu-tree")
    public ApiResponse<List<MenuPermissionTreeResponse>> getMenuPermissionTree() {
        List<MenuPermissionTreeResponse> tree = permissionService.getMenuPermissionTree();
        return ApiResponse.ok(tree);
    }
}
