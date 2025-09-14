package com.server.manage.mapper;

import com.server.manage.dto.menu.MenuQueryRequest;
import com.server.manage.model.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    /**
     * 根据ID查询菜单
     */
    Menu selectById(@Param("id") Long id);
    
    /**
     * 查询所有菜单
     */
    List<Menu> selectAll();
    
    /**
     * 根据父级ID查询子菜单
     */
    List<Menu> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据菜单编码查询菜单
     */
    Menu selectByMenuCode(@Param("menuCode") String menuCode);
    
    /**
     * 根据模块查询菜单
     */
    List<Menu> selectByModule(@Param("module") String module);
    
    /**
     * 插入菜单
     */
    int insert(Menu menu);
    
    /**
     * 更新菜单
     */
    int update(Menu menu);
    
    /**
     * 根据ID删除菜单
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据父级ID删除子菜单
     */
    int deleteByParentId(@Param("parentId") Long parentId);

    /**
     * 分页查询菜单列表
     */
    List<Menu> selectPageList(@Param("query") MenuQueryRequest query,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    /**
     * 查询菜单总数
     */
    Long selectCount(@Param("query") MenuQueryRequest query);
}
