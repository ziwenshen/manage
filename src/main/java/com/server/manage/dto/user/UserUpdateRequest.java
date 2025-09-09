package com.server.manage.dto.user;

import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
