package com.server.manage.service;

import com.server.manage.dto.auth.LoginRequest;
import com.server.manage.dto.auth.LoginResponse;

/**
 * 认证服务接口
 */
public interface IAuthService {
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户登出
     * @param token JWT token
     */
    void logout(String token);
}
