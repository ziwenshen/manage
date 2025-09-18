package com.server.manage.dto.permission;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限响应DTO
 */
@Data
public class PermissionResponse {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 业务模块标识
     */
    private String module;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 操作类型
     */
    private String action;



    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
