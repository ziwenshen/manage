package com.server.manage.interceptor;

import com.server.manage.annotation.RequiresPermissions;
import com.server.manage.service.PermissionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;

// 添加NonNull注解的导入
import org.springframework.lang.NonNull;

/**
 * 权限拦截器
 * 用于处理@RequiresPermissions注解的权限验证
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private PermissionRedisService permissionRedisService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 如果不是处理方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresPermissions requiresPermissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
        
        // 如果没有权限注解，直接放行
        if (requiresPermissions == null) {
            requiresPermissions = handlerMethod.getBeanType().getAnnotation(RequiresPermissions.class);
            if (requiresPermissions == null) {
                return true;
            }
        }

        // 从请求中获取用户ID（实际项目中应该从token或session中获取）
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 获取用户权限
        Set<String> userPermissions = permissionRedisService.getUserPermissions(userId);

        // 验证权限
        String[] permissions = requiresPermissions.value();
        RequiresPermissions.Logical logical = requiresPermissions.logical();

        boolean hasPermission = checkPermission(userPermissions, permissions, logical);
        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

    /**
     * 从请求中获取用户ID
     * 实际项目中应该从token或session中获取
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 示例：从请求头中获取用户ID
        String userIdStr = request.getHeader("User-ID");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.valueOf(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 检查用户权限
     * @param userPermissions 用户拥有的权限
     * @param permissions 需要的权限
     * @param logical 验证逻辑
     * @return 是否有权限
     */
    private boolean checkPermission(Set<String> userPermissions, String[] permissions, RequiresPermissions.Logical logical) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        switch (logical) {
            case AND:
                // 需要满足所有权限
                for (String permission : permissions) {
                    if (!userPermissions.contains(permission)) {
                        return false;
                    }
                }
                return true;
            case OR:
                // 满足任一权限即可
                for (String permission : permissions) {
                    if (userPermissions.contains(permission)) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }
}