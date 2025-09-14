package com.server.manage.controller.perm;

import com.server.manage.common.ApiResponse;
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

    @GetMapping("/list")
    public ApiResponse<List<Permission>> listPermissions() {
        List<Permission> permissions = permissionService.listAll();
        return ApiResponse.ok(permissions);
    }

    @PostMapping("/create")
    public ApiResponse<String> createPermission(@RequestBody Permission permission) {
        permissionService.create(permission);
        return ApiResponse.ok("权限创建成功");
    }

    @PutMapping("/update")
    public ApiResponse<String> updatePermission(@RequestBody Permission permission) {
        permissionService.update(permission);
        return ApiResponse.ok("权限更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.ok("权限删除成功");
    }
}
