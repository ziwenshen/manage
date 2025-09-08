package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户权限关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_permission")
public class UserPermission extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Long permissionId;

    /**
     * 授权类型：1=追加, 2=覆盖
     */
    @TableField("grant_type")
    private Integer grantType;
}