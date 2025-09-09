package com.server.manage.service;

import com.server.manage.dto.user.UserCreateRequest;
import com.server.manage.dto.user.UserUpdateRequest;
import com.server.manage.dto.user.UserQueryRequest;
import com.server.manage.dto.user.UserResponse;

import java.util.List;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 创建用户
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * 根据ID获取用户
     */
    UserResponse getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    UserResponse getUserByUsername(String username);

    /**
     * 更新用户
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 分页查询用户列表
     */
    List<UserResponse> getUserList(UserQueryRequest request);

    /**
     * 查询用户总数
     */
    Long getUserCount(UserQueryRequest request);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
}
