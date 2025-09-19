package com.server.manage.dto.menu;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限树响应DTO
 * 用于角色分配权限时的菜单权限选择
 */
@Data
public class MenuPermissionTreeResponse {

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
    private Integer nodeType;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * URL路径
     */
    private String url;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 前端组件路径
     */
    private String path;

    /**
     * 额外元数据 (JSON格式)
     */
    private String meta;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 该菜单下的权限列表
     */
    private List<PermissionInfo> permissions = new ArrayList<>();

    /**
     * 子菜单列表
     */
    private List<MenuPermissionTreeResponse> children = new ArrayList<>();

    /**
     * 权限信息
     */
    @Data
    public static class PermissionInfo {
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
         * 操作类型
         */
        private String action;

        /**
         * URL路径
         */
        private String url;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;
    }
}
