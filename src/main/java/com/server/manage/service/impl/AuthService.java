package com.server.manage.service.impl;

import com.server.manage.dto.auth.LoginRequest;
import com.server.manage.dto.auth.LoginResponse;
import com.server.manage.dto.user.UserResponse;
import com.server.manage.service.IAuthService;
import com.server.manage.service.IUserService;
import com.server.manage.service.JwtSessionService;
import com.server.manage.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final IUserService userService;
    private final JwtSessionService jwtSessionService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 验证用户凭据
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 获取用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // 获取用户信息
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
