package com.server.manage.security;

import com.server.manage.service.IPermissionService;
import com.server.manage.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * 自定义权限评估器
 * 集成到Spring Security的权限检查机制中
 */
@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final IPermissionService permissionService;
    private final IUserService userService;

    public CustomPermissionEvaluator(IPermissionService permissionService, @Lazy IUserService userService) {
        this.permissionService = permissionService;
        this.userService = userService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        return checkPermission(authentication, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        return checkPermission(authentication, permission.toString());
    }

    /**
     * 检查用户是否拥有指定权限
     * @param authentication 认证信息
     * @param permissionCode 权限码
     * @return 是否拥有权限
     */
    private boolean checkPermission(Authentication authentication, String permissionCode) {
        try {
            // 获取用户名
            String username = null;
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            }

            if (username == null) {
                log.warn("无法获取用户名，权限检查失败");
                return false;
            }

            // 获取用户信息
            var user = userService.getUserByUsername(username);
            if (user == null) {
                log.warn("用户不存在: {}", username);
                return false;
            }

            // 获取用户权限
            Set<String> userPermissions = permissionService.loadUserPermissions(user.getId());
            
            // 检查权限
            boolean hasPermission = userPermissions.contains(permissionCode);
            
            if (!hasPermission) {
                log.debug("用户 {} 缺少权限: {}", username, permissionCode);
            }
            
            return hasPermission;

        } catch (Exception e) {
            log.error("权限检查异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查用户是否拥有多个权限中的任意一个
     * @param authentication 认证信息
     * @param permissionCodes 权限码数组
     * @return 是否拥有任意权限
     */
    public boolean hasAnyPermission(Authentication authentication, String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        for (String permissionCode : permissionCodes) {
            if (checkPermission(authentication, permissionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户是否拥有所有指定权限
     * @param authentication 认证信息
     * @param permissionCodes 权限码数组
     * @return 是否拥有所有权限
     */
    public boolean hasAllPermissions(Authentication authentication, String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        for (String permissionCode : permissionCodes) {
            if (!checkPermission(authentication, permissionCode)) {
                return false;
            }
        }
        return true;
    }
}
