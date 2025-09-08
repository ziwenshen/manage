package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface UserRoleMapper {
    /**
     * 根据用户ID获取角色ID集合
     */
    Set<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID获取所有属于该角色的用户ID集合（用于在角色权限变化时刷新用户权限缓存）
     */
    Set<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
}
