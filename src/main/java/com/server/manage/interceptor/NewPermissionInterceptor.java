package com.server.manage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.manage.annotation.HasPermission;
import com.server.manage.common.ApiResponse;
import com.server.manage.security.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 新的权限拦截器
 * 处理@HasPermission注解的权限验证
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewPermissionInterceptor implements HandlerInterceptor {

    private final CustomPermissionEvaluator permissionEvaluator;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, 
                             @org.springframework.lang.NonNull HttpServletResponse response, 
                             @org.springframework.lang.NonNull Object handler) throws Exception {
        // 如果不是处理方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 先检查方法级别的权限注解
        HasPermission methodPermission = handlerMethod.getMethodAnnotation(HasPermission.class);
        HasPermission classPermission = handlerMethod.getBeanType().getAnnotation(HasPermission.class);
        
        // 方法级别的注解优先于类级别的注解
        HasPermission hasPermission = methodPermission != null ? methodPermission : classPermission;
        
        // 如果没有权限注解，直接放行
        if (hasPermission == null) {
            return true;
        }

        // 执行权限检查
        return checkPermission(hasPermission, handlerMethod.getMethod(), request, response);
    }

    /**
     * 执行权限检查
     * @param hasPermission 权限注解
     * @param method 方法
     * @param request 请求
     * @param response 响应
     * @return 是否有权限
     */
    private boolean checkPermission(HasPermission hasPermission, Method method, 
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // 检查是否允许匿名访问
            if (authentication == null || !authentication.isAuthenticated()) {
                if (hasPermission.allowAnonymous()) {
                    log.debug("方法 {} 允许匿名访问", method.getName());
                    return true;
                } else {
                    log.warn("用户未认证，无法访问方法: {}", method.getName());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
            }

            // 获取需要的权限
            String[] permissions = hasPermission.value();
            if (permissions.length == 0) {
                log.debug("方法 {} 无权限要求", method.getName());
                return true;
            }

            // 根据逻辑类型检查权限
            boolean hasAccess;
            if (hasPermission.logical() == HasPermission.Logical.OR) {
                hasAccess = permissionEvaluator.hasAnyPermission(authentication, permissions);
            } else {
                hasAccess = permissionEvaluator.hasAllPermissions(authentication, permissions);
            }

            if (!hasAccess) {
                String permissionStr = String.join(", ", permissions);
                String logicalStr = hasPermission.logical().name();
                String description = hasPermission.description().isEmpty() ?
                    "访问方法: " + method.getName() : hasPermission.description();

                log.warn("权限不足 - 用户: {}, 需要权限: {} ({}), 描述: {}",
                    authentication.getName(), permissionStr, logicalStr, description);

                // 返回标准的JSON错误响应
                writeErrorResponse(response, permissionStr, description);
                return false;
            }

            log.debug("权限检查通过 - 用户: {}, 方法: {}", authentication.getName(), method.getName());
            return true;

        } catch (Exception e) {
            log.error("权限检查异常: {}", e.getMessage(), e);
            writeErrorResponse(response, "系统错误", "权限检查时发生异常");
            return false;
        }
    }

    /**
     * 写入错误响应
     * @param response HTTP响应
     * @param permission 缺失的权限（仅用于日志，不返回给客户端）
     * @param description 描述
     */
    private void writeErrorResponse(HttpServletResponse response, String permission, String description) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            // 不暴露具体权限信息，只返回通用的权限不足提示
            ApiResponse<Object> errorResponse = ApiResponse.error("权限不足，无法执行此操作");

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

        } catch (IOException e) {
            log.error("写入错误响应失败: {}", e.getMessage(), e);
        }
    }
}
