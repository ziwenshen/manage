package com.server.manage.service.impl;

import com.server.manage.mapper.UserMapper;
import com.server.manage.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 用户详情服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户详情: {}", username);

        try {
            // 直接从数据库获取用户实体（包含密码）
            User userEntity = userMapper.selectByUsername(username);
            
            if (userEntity == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }

            if (!userEntity.getEnabled()) {
                throw new UsernameNotFoundException("用户已被禁用: " + username);
            }

            // 创建Spring Security的UserDetails对象
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userEntity.getUsername())
                    .password(userEntity.getPassword())
                    .disabled(!userEntity.getEnabled())
                    .authorities(new ArrayList<>()) // 这里可以添加用户权限
                    .build();

        } catch (Exception e) {
            log.error("加载用户详情失败: {}", username, e);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
    }
}
