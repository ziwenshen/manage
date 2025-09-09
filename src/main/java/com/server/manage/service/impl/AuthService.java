package com.server.manage.service.impl;

import com.server.manage.dto.auth.LoginRequest;
import com.server.manage.dto.auth.LoginResponse;
import com.server.manage.dto.user.UserResponse;
import com.server.manage.mapper.UserMapper;
import com.server.manage.model.User;
import com.server.manage.service.IAuthService;
import com.server.manage.service.IUserService;
import com.server.manage.service.JwtSessionService;
import com.server.manage.util.JwtTokenUtil;
import com.server.manage.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final IUserService userService;
    private final JwtSessionService jwtSessionService;
    private final UserMapper userMapper;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadCredentialsException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadCredentialsException("密码不能为空");
        }

        // 从数据库获取用户信息（包含加密密码）
        User userEntity = userMapper.selectByUsername(request.getUsername().trim());
        if (userEntity == null) {
            log.warn("用户不存在: {}", request.getUsername());
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 检查用户是否启用
        if (!userEntity.getEnabled()) {
            log.warn("用户已被禁用: {}", request.getUsername());
            throw new BadCredentialsException("用户已被禁用");
        }

        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), userEntity.getPassword())) {
            log.warn("密码错误: {}", request.getUsername());
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 获取用户详情（用于生成JWT）
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // 获取用户响应信息
        UserResponse user = userService.getUserByUsername(request.getUsername());
        
        // 生成JWT token
        String accessToken = jwtTokenUtil.generateToken(userDetails);

        // 保存会话信息到Redis
        jwtSessionService.saveSession(user, accessToken, "Web Client");

        log.info("用户登录成功: {}", request.getUsername());

        // 构建登录响应
        return new LoginResponse(
                accessToken,
                jwtExpiration,
                user.getUsername(),
                user.getId()
        );
    }

    @Override
    public void logout(String token) {
        // 从Redis中删除会话信息
        jwtSessionService.removeSession(token);
        log.info("用户登出成功，token: {}", token.substring(0, 10) + "...");
    }
}
