package com.server.manage.service;

import com.server.manage.model.Menu;

import java.util.List;

public interface IMenuService {
    /**
     * 查询所有菜单
     */
    List<Menu> listAll();
    
    /**
     * 根据ID查询菜单
     */
    Menu getById(Long id);
    
    /**
     * 根据菜单编码查询菜单
     */
    Menu getByMenuCode(String menuCode);
    
    /**
     * 根据父级ID查询子菜单
     */
    List<Menu> listByParentId(Long parentId);
    
    /**
     * 根据模块查询菜单
     */
    List<Menu> listByModule(String module);
    
    /**
     * 获取菜单树结构
     */
    List<Menu> getMenuTree();
    
    /**
     * 创建菜单
     */
    Long create(Menu menu);
    
    /**
     * 更新菜单
     */
    void update(Menu menu);
    
    /**
     * 删除菜单（级联删除子菜单）
     */
    void delete(Long id);
    
    /**
     * 批量删除菜单
     */
    void deleteBatch(List<Long> ids);
}
