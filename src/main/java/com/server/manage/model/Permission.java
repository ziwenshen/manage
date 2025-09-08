package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
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
     * 菜单或页面的唯一编码
     */
    @TableField("menu_code")
    private String menuCode;

    /**
     * 按钮/操作
     */
    @TableField("action")
    private String action;

    /**
     * 后端接口路径
     */
    @TableField("url")
    private String url;
}