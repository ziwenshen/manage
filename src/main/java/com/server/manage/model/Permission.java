package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 对应数据库表: permission
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class Permission extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 权限标识，如 order:order-list:add
     */
    @TableField("code")
    private String code;

    /**
     * 中文名，如 订单列表-新增
     */
    @TableField("name")
    private String name;

    /**
     * 业务模块标识
     */
    @TableField("module")
    private String module;

    /**
     * 菜单编码
     */
    @TableField("menu_code")
    private String menuCode;

    /**
     * 操作类型
     */
    @TableField("action")
    private String action;

    /**
     * URL路径
     */
    @TableField("url")
    private String url;
}