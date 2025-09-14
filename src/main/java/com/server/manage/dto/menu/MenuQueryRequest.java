package com.server.manage.dto.menu;

import lombok.Data;

/**
 * 菜单查询请求DTO
 */
@Data
public class MenuQueryRequest {

    /**
     * 菜单名称（模糊查询）
     */
    private String name;

    /**
     * 菜单编码（模糊查询）
     */
    private String menuCode;

    /**
     * 业务模块标识
     */
    private String module;

    /**
     * 节点类型：1=文件夹(folder), 2=页面(page), 3=按钮(button)
     */
    private Integer nodeType;

    /**
     * 父级菜单ID
     */
    private Long parentId;

    /**
     * 页码，从1开始
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
