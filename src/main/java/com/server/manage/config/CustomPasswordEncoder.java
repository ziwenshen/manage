package com.server.manage.config;

import com.server.manage.util.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码编码器，使用简单的SHA-256加密方式
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return PasswordUtil.encode(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return PasswordUtil.matches(rawPassword.toString(), encodedPassword);
    }
}