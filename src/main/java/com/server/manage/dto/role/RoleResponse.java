package com.server.manage.dto.role;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色响应DTO
 */
@Data
public class RoleResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
