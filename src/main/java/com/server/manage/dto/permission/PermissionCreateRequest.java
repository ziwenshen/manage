package com.server.manage.dto.permission;

import lombok.Data;



/**
 * 权限创建请求DTO
 */
@Data
public class PermissionCreateRequest {

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


}
