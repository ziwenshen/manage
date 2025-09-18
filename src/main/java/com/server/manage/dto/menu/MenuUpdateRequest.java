package com.server.manage.dto.menu;

import lombok.Data;

/**
 * 菜单更新请求DTO
 */
@Data
public class MenuUpdateRequest {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父级菜单ID
     */
    private Long parentId;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 业务模块标识
     */
    private String module;

    /**
     * 节点类型：1=文件夹(folder), 2=页面(page), 3=按钮(button)
     */
    private Integer nodeType = 2;

    /**
     * 排序
     */
    private Integer sortOrder = 0;

    /**
     * URL路径
     */
    private String url;
}
