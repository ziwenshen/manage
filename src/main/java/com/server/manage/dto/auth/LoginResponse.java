package com.server.manage.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间(秒)
     */
    private Long expiresIn;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    public LoginResponse(String accessToken, Long expiresIn, String username, Long userId) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.userId = userId;
    }
}
