package com.server.manage.mapper;

import com.server.manage.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role selectById(@Param("id") Long id);
    List<Role> selectAll();
    int insert(Role role);
    int update(Role role);
    int deleteById(@Param("id") Long id);
}
