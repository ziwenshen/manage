package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.dto.common.PageResult;
import com.server.manage.dto.menu.MenuPermissionTreeResponse;
import com.server.manage.dto.permission.*;
import com.server.manage.model.Permission;
import com.server.manage.service.IPermissionService;
import org.springframework.beans.BeanUtils;
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

    /**
     * 分页查询权限列表
     */
    @HasPermission(value = "menu:permission:view", description = "查看权限列表")
    @GetMapping("/list")
    public ApiResponse<PageResult<PermissionResponse>> getPermissionList(PermissionQueryRequest request) {
        List<PermissionResponse> permissions = permissionService.getPermissionList(request);
        Long total = permissionService.getPermissionCount(request);

        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;

        PageResult<PermissionResponse> result = PageResult.of(permissions, total, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 获取权限树形结构（按菜单层级组织）
     */
    @HasPermission(value = "menu:permission:view", description = "查看权限树")
    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeResponse>> getPermissionTree() {
        List<PermissionTreeResponse> tree = permissionService.getPermissionTree();
        return ApiResponse.ok(tree);
    }

    /**
     * 创建权限
     */
    @HasPermission(value = "menu:permission:add", description = "创建权限")
    @PostMapping("/create")
    public ApiResponse<String> createPermission(@RequestBody PermissionCreateRequest request) {
        // 检查权限编码是否已存在
        Permission existingPermission = permissionService.getByCode(request.getCode());
        if (existingPermission != null) {
            return ApiResponse.error(400, "权限编码已存在：" + request.getCode());
        }

        // 转换为Permission实体
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);

        permissionService.create(permission);
        return ApiResponse.ok("权限创建成功");
    }

    /**
     * 更新权限
     */
    @HasPermission(value = "menu:permission:edit", description = "编辑权限")
    @PutMapping("/update")
    public ApiResponse<String> updatePermission(@RequestBody PermissionUpdateRequest request) {
        // 检查权限是否存在
        Permission existingPermission = permissionService.getById(request.getId());
        if (existingPermission == null) {
            return ApiResponse.error(404, "权限不存在");
        }

        // 检查权限编码是否被其他权限使用
        Permission permissionWithSameCode = permissionService.getByCode(request.getCode());
        if (permissionWithSameCode != null && !permissionWithSameCode.getId().equals(request.getId())) {
            return ApiResponse.error(400, "权限编码已被其他权限使用：" + request.getCode());
        }

        // 转换为Permission实体
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);

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
