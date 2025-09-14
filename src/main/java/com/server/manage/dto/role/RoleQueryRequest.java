package com.server.manage.dto.role;

import lombok.Data;

/**
 * 角色查询请求DTO
 */
@Data
public class RoleQueryRequest {

    /**
     * 角色名称（支持模糊查询）
     */
    private String name;

    /**
     * 角色编码（支持模糊查询）
     */
    private String code;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
