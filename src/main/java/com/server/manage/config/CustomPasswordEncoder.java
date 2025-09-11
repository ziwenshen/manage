package com.server.manage.config;

import com.server.manage.util.PasswordUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码编码器，支持BCrypt和SHA-256两种加密方式
 * 新密码使用BCrypt加密，旧密码(SHA-256)也能验证通过
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 首先尝试使用BCrypt验证
        if (bcryptEncoder.matches(rawPassword, encodedPassword)) {
            return true;
        }
        
        // 如果BCrypt验证失败，尝试使用旧的SHA-256方式验证
        try {
            return PasswordUtil.matches(rawPassword.toString(), encodedPassword);
        } catch (Exception e) {
            // 如果两种方式都失败，返回false
            return false;
        }
    }
}