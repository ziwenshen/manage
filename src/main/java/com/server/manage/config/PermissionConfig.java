package com.server.manage.config;

import com.server.manage.interceptor.NewPermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限配置类
 * 注册新的权限拦截器
 */
@Configuration
@RequiredArgsConstructor
public class PermissionConfig implements WebMvcConfigurer {

    private final NewPermissionInterceptor newPermissionInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 注册新的权限拦截器，拦截所有请求
        registry.addInterceptor(newPermissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**") // 排除认证相关接口
                .excludePathPatterns("/error") // 排除错误页面
                .excludePathPatterns("/favicon.ico"); // 排除图标请求
    }
}
