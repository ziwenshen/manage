package com.server.manage.mapper;

import com.server.manage.dto.role.RoleQueryRequest;
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

    /**
     * 分页查询角色列表
     */
    List<Role> selectPageList(@Param("query") RoleQueryRequest query,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    /**
     * 查询角色总数
     */
    Long selectCount(@Param("query") RoleQueryRequest query);
}
