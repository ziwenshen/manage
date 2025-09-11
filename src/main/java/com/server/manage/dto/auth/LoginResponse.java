package com.server.manage.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 登录响应DTO - 完整版
 * 包含用户基本信息、权限信息等完整登录信息
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
     * 用户基本信息
     */
    private UserInfo userInfo;
    
    /**
     * 权限信息
     */
    private PermissionInfo permissionInfo;
    
    /**
     * 登录状态信息
     */
    private LoginStatusInfo loginStatusInfo;
    
    // 为了保持兼容性，保留原有的构造函数
    public LoginResponse(String accessToken, Long expiresIn, String username, Long userId) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
        // 创建基本的用户信息
        this.userInfo = new UserInfo();
        this.userInfo.setUserId(userId);
        this.userInfo.setUsername(username);
        // 创建空的权限信息和登录状态信息
        this.permissionInfo = new PermissionInfo();
        this.loginStatusInfo = new LoginStatusInfo();
        this.loginStatusInfo.setLoginTime(LocalDateTime.now());
    }
    
    /**
     * 用户基本信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long userId;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 是否启用
         */
        private Boolean enabled;
        
        /**
         * 创建时间
         */
        private LocalDateTime createdAt;
    }
    
    /**
     * 权限信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfo {
        /**
         * 用户角色ID列表
         */
        private Set<Long> roleIds;
        
        /**
         * 用户角色名称列表
         */
        private List<String> roleNames;
        
        /**
         * 用户权限码列表
         */
        private Set<String> permissions;
        
        /**
         * 用户可访问的菜单列表（如果需要）
         */
        private List<String> menus;
    }
    
    /**
     * 登录状态信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginStatusInfo {
        /**
         * 登录时间
         */
        private LocalDateTime loginTime;
        
        /**
         * 登录IP（可选）
         */
        private String loginIp;
        
        /**
         * 客户端信息
         */
        private String clientInfo;
        
        /**
         * 会话ID
         */
        private String sessionId;
    }
}
