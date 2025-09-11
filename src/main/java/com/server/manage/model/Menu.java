package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体类
 * 对应数据库表: menu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu")
public class Menu extends BaseEntity { 
    private static final long serialVersionUID = 1L;

    /**
     * 父级菜单ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 菜单编码
     */
    @TableField("menu_code")
    private String menuCode;

    /**
     * 菜单名称
     */
    @TableField("name")
    private String name;

    /**
     * 业务模块标识
     */
    @TableField("module")
    private String module;

    /**
     * 节点类型：1=文件夹(folder), 2=页面(page), 3=按钮(button)
     */
    @TableField("node_type")
    private Integer nodeType = 2;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder = 0;
}