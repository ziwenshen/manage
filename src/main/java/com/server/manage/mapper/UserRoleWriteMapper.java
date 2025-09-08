package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleWriteMapper {
    int deleteByUserId(@Param("userId") Long userId);
    int batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
