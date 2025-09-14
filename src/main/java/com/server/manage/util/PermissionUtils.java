package com.server.manage.util;

import com.server.manage.service.IPermissionService;
import com.server.manage.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 权限工具类
 * 提供便捷的权限检查方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionUtils {

    private final IPermissionService permissionService;
    private final IUserService userService;

    /**
     * 检查当前用户是否拥有指定权限
     * @param permissionCode 权限码
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permissionCode) {
        return hasPermission(getCurrentAuthentication(), permissionCode);
    }

    /**
     * 检查指定用户是否拥有指定权限
     * @param authentication 认证信息
     * @param permissionCode 权限码
     * @return 是否拥有权限
     */
    public boolean hasPermission(Authentication authentication, String permissionCode) {
        if (authentication == null || permissionCode == null) {
            return false;
        }

        try {
            String username = extractUsername(authentication);
            if (username == null) {
                return false;
            }

            var user = userService.getUserByUsername(username);
            if (user == null) {
                return false;
            }

            Set<String> userPermissions = permissionService.loadUserPermissions(user.getId());
            return userPermissions.contains(permissionCode);

        } catch (Exception e) {
            log.error("权限检查异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查当前用户是否拥有任意一个权限
     * @param permissionCodes 权限码数组
     * @return 是否拥有任意权限
     */
    public boolean hasAnyPermission(String... permissionCodes) {
        return hasAnyPermission(getCurrentAuthentication(), permissionCodes);
    }

    /**
     * 检查指定用户是否拥有任意一个权限
     * @param authentication 认证信息
     * @param permissionCodes 权限码数组
     * @return 是否拥有任意权限
     */
    public boolean hasAnyPermission(Authentication authentication, String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        for (String permissionCode : permissionCodes) {
            if (hasPermission(authentication, permissionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否拥有所有权限
     * @param permissionCodes 权限码数组
     * @return 是否拥有所有权限
     */
    public boolean hasAllPermissions(String... permissionCodes) {
        return hasAllPermissions(getCurrentAuthentication(), permissionCodes);
    }

    /**
     * 检查指定用户是否拥有所有权限
     * @param authentication 认证信息
     * @param permissionCodes 权限码数组
     * @return 是否拥有所有权限
     */
    public boolean hasAllPermissions(Authentication authentication, String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        for (String permissionCode : permissionCodes) {
            if (!hasPermission(authentication, permissionCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前用户的所有权限
     * @return 权限集合
     */
    public Set<String> getCurrentUserPermissions() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication == null) {
            return Set.of();
        }

        try {
            String username = extractUsername(authentication);
            if (username == null) {
                return Set.of();
            }

            var user = userService.getUserByUsername(username);
            if (user == null) {
                return Set.of();
            }

            return permissionService.loadUserPermissions(user.getId());

        } catch (Exception e) {
            log.error("获取用户权限异常: {}", e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * 获取当前认证信息
     * @return 认证信息
     */
    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从认证信息中提取用户名
     * @param authentication 认证信息
     * @return 用户名
     */
    private String extractUsername(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }
}
