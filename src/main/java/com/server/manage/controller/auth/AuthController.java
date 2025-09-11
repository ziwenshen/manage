package com.server.manage.controller.auth;

import com.server.manage.common.ApiResponse;
import com.server.manage.dto.auth.LoginRequest;
import com.server.manage.dto.auth.LoginResponse;
import com.server.manage.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * 用户登录 - 返回完整用户信息和权限
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("用户登录请求: {}", request.getUsername());
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        
        try {
            LoginResponse response = authService.login(request);
            
            // 设置客户端IP到登录状态信息中
            if (response.getLoginStatusInfo() != null) {
                response.getLoginStatusInfo().setLoginIp(getClientIp(httpRequest));
            }
            
            log.info("用户登录成功: {}", request.getUsername());
            return ApiResponse.ok(response);
        } catch (Exception e) {
            log.error("用户登录失败: {}, 错误: {}", request.getUsername(), e.getMessage());
            return ApiResponse.error(401, "用户名或密码错误");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(400, "缺少认证令牌");
        }
        
        String token = authHeader.substring(7);
        
        try {
            authService.logout(token);
            log.info("用户登出成功");
            return ApiResponse.ok("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage(), e);
            return ApiResponse.error(500, "登出失败");
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
