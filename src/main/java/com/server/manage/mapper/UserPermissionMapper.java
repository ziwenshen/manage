package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface UserPermissionMapper {
    /**
     * 根据用户ID获取直赋权限ID集合
     */
    Set<Long> selectPermissionIdsByUserId(@Param("userId") Long userId);
}
