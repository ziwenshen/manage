package com.server.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // minimal placeholder: if needed, implement later
    Long selectIdByUsername(@Param("username") String username);
}
