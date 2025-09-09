package com.server.manage.security.filter;

import com.server.manage.service.JwtSessionService;
import com.server.manage.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final JwtSessionService jwtSessionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT token
        jwt = authHeader.substring(7);
        
        try {
            // 从JWT中提取用户名
            username = jwtTokenUtil.getUsernameFromToken(jwt);

            // 如果用户名不为空且当前没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 检查Redis中的会话是否有效
                if (!jwtSessionService.isValidSession(jwt)) {
                    log.warn("会话已失效: {}", username);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 加载用户详情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 验证token是否有效
                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    // 刷新会话过期时间
                    jwtSessionService.refreshSession(jwt);

                    // 创建认证token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // 设置到安全上下文中
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
