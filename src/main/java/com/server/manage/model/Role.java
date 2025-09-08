package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
public class Role extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色编码
     */
    @TableField("code")
    private String code;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;
}