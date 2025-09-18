package com.server.manage.dto.permission;

import lombok.Data;

/**
 * 权限查询请求DTO
 */
@Data
public class PermissionQueryRequest {

    /**
     * 权限名称（模糊查询）
     */
    private String name;

    /**
     * 权限编码（模糊查询）
     */
    private String code;

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
     * 页码，从1开始
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
