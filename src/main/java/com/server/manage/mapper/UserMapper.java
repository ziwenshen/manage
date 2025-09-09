package com.server.manage.mapper;

import com.server.manage.model.User;
import com.server.manage.dto.user.UserQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户名查询用户ID
     */
    Long selectIdByUsername(@Param("username") String username);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int updateById(User user);

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 分页查询用户列表
     */
    List<User> selectPageList(@Param("query") UserQueryRequest query, 
                             @Param("offset") Integer offset, 
                             @Param("limit") Integer limit);

    /**
     * 查询用户总数
     */
    Long selectCount(@Param("query") UserQueryRequest query);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);
}
