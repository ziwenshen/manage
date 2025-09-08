package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionWriteMapper {
    /**
     * 删除角色下所有权限关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色-权限关联
     */
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
