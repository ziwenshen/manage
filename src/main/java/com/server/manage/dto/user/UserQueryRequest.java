package com.server.manage.dto.user;

import lombok.Data;

/**
 * 用户查询请求DTO
 */
@Data
public class UserQueryRequest {

    /**
     * 用户名（支持模糊查询）
     */
    private String username;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
