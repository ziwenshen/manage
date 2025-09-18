package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.dto.common.PageResult;
import com.server.manage.dto.menu.MenuCreateRequest;
import com.server.manage.dto.menu.MenuQueryRequest;
import com.server.manage.dto.menu.MenuResponse;
import com.server.manage.dto.menu.MenuUpdateRequest;
import com.server.manage.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单相关接口（占位）
 */
@RestController
@RequestMapping("/permissions/menus")
public class MenuController {

    @Autowired
    private com.server.manage.service.IMenuService menuService;

    /**
     * 分页查询菜单列表
     */
    @HasPermission(value = "menu:menu:view", description = "查看菜单列表")
    @GetMapping("/list")
    public ApiResponse<PageResult<MenuResponse>> getMenuList(MenuQueryRequest request) {
        List<MenuResponse> menus = menuService.getMenuList(request);
        Long total = menuService.getMenuCount(request);

        Integer page = request.getPage() != null ? request.getPage() : 1;
        Integer size = request.getSize() != null ? request.getSize() : 10;

        PageResult<MenuResponse> result = PageResult.of(menus, total, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 获取菜单树结构（不分页）
     */
    @HasPermission(value = "menu:menu:view", description = "查看菜单树")
    @GetMapping("/tree")
    public ApiResponse<List<MenuResponse>> getMenuTree() {
        List<MenuResponse> menus = menuService.getMenuTree();
        return ApiResponse.ok(menus);
    }

    @HasPermission(value = "menu:menu:add", description = "创建菜单")
    @PostMapping("/create")
    public ApiResponse<String> createMenu(@RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
        return ApiResponse.ok("菜单创建成功");
    }

    @HasPermission(value = "menu:menu:edit", description = "编辑菜单")
    @PutMapping("/update")
    public ApiResponse<String> updateMenu(@RequestBody MenuUpdateRequest request) {
        menuService.updateMenu(request);
        return ApiResponse.ok("菜单更新成功");
    }

    @HasPermission(value = "menu:menu:delete", description = "删除菜单")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteMenu(@PathVariable Long id) {
    menuService.delete(id);
        return ApiResponse.ok("菜单删除成功");
    }
}
