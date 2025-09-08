package com.server.manage.controller.perm;

import com.server.manage.common.ApiResponse;
import com.server.manage.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单相关接口（占位）
 */
@RestController
@RequestMapping("/api/permissions/menus")
public class MenuController {

    @Autowired
    private com.server.manage.service.IMenuService menuService;

    @GetMapping("/list")
    public ApiResponse<List<Menu>> listMenus() {
    List<Menu> menus = menuService.listAll();
        return ApiResponse.ok(menus);
    }

    @PostMapping("/create")
    public ApiResponse<String> createMenu(@RequestBody Menu menu) {
    menuService.create(menu);
        return ApiResponse.ok("菜单创建成功");
    }

    @PutMapping("/update")
    public ApiResponse<String> updateMenu(@RequestBody Menu menu) {
    menuService.update(menu);
        return ApiResponse.ok("菜单更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteMenu(@PathVariable Long id) {
    menuService.delete(id);
        return ApiResponse.ok("菜单删除成功");
    }
}
