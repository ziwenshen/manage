package com.server.manage.service.impl;

import com.server.manage.dto.user.UserCreateRequest;
import com.server.manage.dto.user.UserUpdateRequest;
import com.server.manage.dto.user.UserQueryRequest;
import com.server.manage.dto.user.UserResponse;
import com.server.manage.exception.BusinessException;
import com.server.manage.mapper.UserMapper;
import com.server.manage.model.User;
import com.server.manage.service.IUserService;
import com.server.manage.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 参数校验
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 检查用户名是否已存在
        // if (userMapper.existsByUsername(request.getUsername())) {
        //     throw new BusinessException("用户名已存在");
        // }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        user.setCreatedAt(LocalDateTime.now());

        // 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("用户创建失败");
        }

        // 转换为响应对象
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("用户名不能为空");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 构建更新对象
        User user = new User();
        user.setId(id);
        
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        // 更新用户
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("用户更新失败");
        }

        // 重新查询更新后的用户信息
        User updatedUser = userMapper.selectById(id);
        return convertToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 删除用户
        int result = userMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("用户删除失败");
        }
    }

    @Override
    public List<UserResponse> getUserList(UserQueryRequest request) {
        if (request == null) {
            request = new UserQueryRequest();
        }

        // 计算分页参数
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        int offset = (page - 1) * size;

        // 查询用户列表
        List<User> users = userMapper.selectPageList(request, offset, size);
        
        // 转换为响应对象列表
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserCount(UserQueryRequest request) {
        if (request == null) {
            request = new UserQueryRequest();
        }
        return userMapper.selectCount(request);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return userMapper.existsByUsername(username);
    }

    /**
     * 转换User实体为UserResponse
     */
    private UserResponse convertToResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
