package com.server.manage.service;

import com.server.manage.dto.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT会话管理服务
 * 用于在Redis中管理用户登录会话信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtSessionService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    // Redis会话key前缀
    private static final String SESSION_PREFIX = "jwt:session:";
    // 用户活跃会话key前缀
    private static final String USER_ACTIVE_TOKEN_PREFIX = "jwt:user:active:";

    /**
     * 会话信息DTO
     */
    public static class SessionInfo {
        private Long userId;
        private String username;
        private String token;
        private Long loginTime;
        private Long expiresAt;
        private String clientInfo;

        // Constructors
        public SessionInfo() {}

        public SessionInfo(Long userId, String username, String token, Long loginTime, Long expiresAt, String clientInfo) {
            this.userId = userId;
            this.username = username;
            this.token = token;
            this.loginTime = loginTime;
            this.expiresAt = expiresAt;
            this.clientInfo = clientInfo;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Long getLoginTime() { return loginTime; }
        public void setLoginTime(Long loginTime) { this.loginTime = loginTime; }

        public Long getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }

        public String getClientInfo() { return clientInfo; }
        public void setClientInfo(String clientInfo) { this.clientInfo = clientInfo; }
    }

    /**
     * 存储用户会话信息
     * @param user 用户信息
     * @param token JWT token
     * @param clientInfo 客户端信息
     */
    public void saveSession(UserResponse user, String token, String clientInfo) {
        try {
            long now = System.currentTimeMillis();
            long expiresAt = now + (jwtExpiration * 1000);

            SessionInfo sessionInfo = new SessionInfo(
                    user.getId(),
                    user.getUsername(),
                    token,
                    now,
                    expiresAt,
                    clientInfo
            );

            String sessionKey = SESSION_PREFIX + token;
            String userActiveKey = USER_ACTIVE_TOKEN_PREFIX + user.getId();

            // 存储会话信息
            String sessionJson = objectMapper.writeValueAsString(sessionInfo);
            redisTemplate.opsForValue().set(sessionKey, sessionJson, jwtExpiration, TimeUnit.SECONDS);

            // 存储用户活跃token（用于单点登录控制）
            redisTemplate.opsForValue().set(userActiveKey, token, jwtExpiration, TimeUnit.SECONDS);

            log.info("保存用户会话: userId={}, token={}", user.getId(), token.substring(0, 10) + "...");

        } catch (JsonProcessingException e) {
            log.error("保存用户会话失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存会话失败", e);
        }
    }

    /**
     * 获取会话信息
     * @param token JWT token
     * @return 会话信息
     */
    public SessionInfo getSession(String token) {
        try {
            String sessionKey = SESSION_PREFIX + token;
            String sessionJson = redisTemplate.opsForValue().get(sessionKey);

            if (sessionJson == null) {
                return null;
            }

            return objectMapper.readValue(sessionJson, SessionInfo.class);

        } catch (JsonProcessingException e) {
            log.error("解析会话信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 验证会话是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean isValidSession(String token) {
        SessionInfo session = getSession(token);
        if (session == null) {
            return false;
        }

        // 检查是否过期
        return System.currentTimeMillis() < session.getExpiresAt();
    }

    /**
     * 刷新会话过期时间
     * @param token JWT token
     */
    public void refreshSession(String token) {
        String sessionKey = SESSION_PREFIX + token;
        redisTemplate.expire(sessionKey, jwtExpiration, TimeUnit.SECONDS);
    }

    /**
     * 删除会话
     * @param token JWT token
     */
    public void removeSession(String token) {
        SessionInfo session = getSession(token);
        if (session != null) {
            String sessionKey = SESSION_PREFIX + token;
            String userActiveKey = USER_ACTIVE_TOKEN_PREFIX + session.getUserId();

            redisTemplate.delete(sessionKey);
            redisTemplate.delete(userActiveKey);

            log.info("删除用户会话: userId={}, token={}", session.getUserId(), token.substring(0, 10) + "...");
        }
    }

    /**
     * 删除用户的所有会话（登出所有设备）
     * @param userId 用户ID
     */
    public void removeAllUserSessions(Long userId) {
        String userActiveKey = USER_ACTIVE_TOKEN_PREFIX + userId;
        String activeToken = redisTemplate.opsForValue().get(userActiveKey);
        
        if (activeToken != null) {
            removeSession(activeToken);
        }

        log.info("删除用户所有会话: userId={}", userId);
    }

    /**
     * 获取用户当前活跃的token
     * @param userId 用户ID
     * @return 活跃token
     */
    public String getUserActiveToken(Long userId) {
        String userActiveKey = USER_ACTIVE_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(userActiveKey);
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        String activeToken = getUserActiveToken(userId);
        return activeToken != null && isValidSession(activeToken);
    }
}
