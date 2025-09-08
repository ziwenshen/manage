package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface RolePermissionMapper {
    /**
     * 根据角色ID集合获取权限ID集合
     */
    Set<Long> selectPermissionIdsByRoleIds(@Param("roleIds") java.util.Set<Long> roleIds);
}
