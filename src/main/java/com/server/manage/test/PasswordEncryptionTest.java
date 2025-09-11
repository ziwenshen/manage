package com.server.manage.test;

import com.server.manage.util.PasswordUtil;

/**
 * 密码加密测试程序
 * 用于生成密码的加密字符
 */
public class PasswordEncryptionTest {
    
    public static void main(String[] args) {
        String password = "admin";
        String encryptedPassword = PasswordUtil.encode(password);
        
        System.out.println("原密码: " + password);
        System.out.println("加密后: " + encryptedPassword);
        
        // 验证加密是否正确
        boolean matches = PasswordUtil.matches(password, encryptedPassword);
        System.out.println("验证结果: " + (matches ? "成功" : "失败"));
    }
}
