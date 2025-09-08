package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单操作实体类
 * 对应数据库表: menu_action
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu_action")
public class MenuAction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableField("menu_id")
    private Long menuId;

    /**
     * 操作标识
     */
    @TableField("action")
    private String action;

    /**
     * 操作名称
     */
    @TableField("name")
    private String name;
}