package com.server.manage.model;

import com.baomidou.mybatisplus.annotation.*;
import com.server.manage.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标识(0:未删除, 1:已删除)
     */
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted = 0;

    public String getCreatedAtStr() {
        return DateUtil.formatDateTime(createdAt);
    }

    public String getUpdatedAtStr() {
        return DateUtil.formatDateTime(updatedAt);
    }
}