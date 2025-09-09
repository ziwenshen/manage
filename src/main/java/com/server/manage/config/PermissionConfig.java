package com.server.manage.config;

import com.server.manage.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * 权限配置类
 * 用于注册权限拦截器
 */
@Configuration
public class PermissionConfig implements WebMvcConfigurer {

    @Autowired
    private PermissionInterceptor permissionInterceptor;
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 注册权限拦截器，拦截所有API请求
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/permissions/list")
                .excludePathPatterns("/api/permissions/roles/list")
                .excludePathPatterns("/api/permissions/menus/list");
    }
}
