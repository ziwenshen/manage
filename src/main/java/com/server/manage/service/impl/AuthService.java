package com.server.manage.service.impl;

import com.server.manage.dto.auth.LoginRequest;
import com.server.manage.dto.auth.LoginResponse;
import com.server.manage.dto.user.UserResponse;
import com.server.manage.service.IAuthService;
import com.server.manage.service.IUserService;
import com.server.manage.service.IPermissionService;
import com.server.manage.service.JwtSessionService;
import com.server.manage.util.JwtTokenUtil;
import com.server.manage.mapper.UserRoleMapper;
import com.server.manage.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final IUserService userService;
    private final JwtSessionService jwtSessionService;
    private final IPermissionService permissionService;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 验证用户凭据
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 获取用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // 获取用户信息
        UserResponse user = userService.getUserByUsername(request.getUsername());
        
        // 生成JWT token
        String accessToken = jwtTokenUtil.generateToken(userDetails);

        // 保存会话信息到Redis
        jwtSessionService.saveSession(user, accessToken, "Web Client");

        // 构建完整的登录响应
        LoginResponse response = buildCompleteLoginResponse(user, accessToken, null);

        log.info("用户登录成功: {}, 权限数: {}, 角色数: {}", 
                request.getUsername(), 
                response.getPermissionInfo().getPermissions() != null ? response.getPermissionInfo().getPermissions().size() : 0,
                response.getPermissionInfo().getRoleIds() != null ? response.getPermissionInfo().getRoleIds().size() : 0);

        return response;
    }

    /**
     * 构建完整的登录响应
     */
    private LoginResponse buildCompleteLoginResponse(UserResponse user, String accessToken, String clientIp) {
        // 构建用户基本信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEnabled(),
                user.getCreatedAt()
        );

        // 获取用户权限信息
        Set<String> permissions = permissionService.loadUserPermissions(user.getId());
        Set<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        List<String> roleNames = roleIds.stream()
                .map(roleId -> {
                    try {
                        return roleMapper.selectById(roleId).getName();
                    } catch (Exception e) {
                        return "未知角色";
                    }
                })
                .collect(Collectors.toList());


        // 查询用户有view权限的菜单（文件夹或界面）
        List<com.server.manage.dto.menu.MenuResponse> menus = permissionService.getUserMenusWithViewPermission(user.getId());

        LoginResponse.PermissionInfo permissionInfo = new LoginResponse.PermissionInfo(
                roleIds,
                roleNames,
                permissions,
                menus
        );

        // 构建登录状态信息
        LoginResponse.LoginStatusInfo loginStatusInfo = new LoginResponse.LoginStatusInfo(
                LocalDateTime.now(),
                clientIp,
                "Web Client",
                accessToken.substring(0, Math.min(10, accessToken.length())) + "..."
        );

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtExpiration,
                userInfo,
                permissionInfo,
                loginStatusInfo
        );
    }

    @Override
    public void logout(String token) {
        // 从Redis中删除会话信息
        jwtSessionService.removeSession(token);
        log.info("用户登出成功，token: {}", token.substring(0, 10) + "...");
    }
}
