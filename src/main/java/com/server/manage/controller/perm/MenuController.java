package com.server.manage.controller.perm;

import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
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

    @HasPermission(value = "menu:menu:view", description = "查看菜单列表")
    @GetMapping("/list")
    public ApiResponse<List<Menu>> listMenus() {
    List<Menu> menus = menuService.listAll();
        return ApiResponse.ok(menus);
    }

    @HasPermission(value = "menu:menu:add", description = "创建菜单")
    @PostMapping("/create")
    public ApiResponse<String> createMenu(@RequestBody Menu menu) {
    menuService.create(menu);
        return ApiResponse.ok("菜单创建成功");
    }

    @HasPermission(value = "menu:menu:edit", description = "编辑菜单")
    @PutMapping("/update")
    public ApiResponse<String> updateMenu(@RequestBody Menu menu) {
    menuService.update(menu);
        return ApiResponse.ok("菜单更新成功");
    }

    @HasPermission(value = "menu:menu:delete", description = "删除菜单")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteMenu(@PathVariable Long id) {
    menuService.delete(id);
        return ApiResponse.ok("菜单删除成功");
    }
}
