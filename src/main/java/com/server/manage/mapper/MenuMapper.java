package com.server.manage.mapper;

import com.server.manage.model.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    Menu selectById(@Param("id") Long id);
    List<Menu> selectAll();
    int insert(Menu menu);
    int update(Menu menu);
    int deleteById(@Param("id") Long id);
}
