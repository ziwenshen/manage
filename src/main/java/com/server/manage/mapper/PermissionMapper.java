package com.server.manage.mapper;

import com.server.manage.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    Permission selectById(@Param("id") Long id);

    /**
     * 根据一组ID批量查询 Permission
     */
    List<Permission> selectByIds(@Param("ids") java.util.Set<Long> ids);

    List<Permission> selectAll();

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(@Param("id") Long id);
}
