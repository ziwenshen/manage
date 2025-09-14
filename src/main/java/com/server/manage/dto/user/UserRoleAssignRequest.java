package com.server.manage.dto.user;

import lombok.Data;

import java.util.List;

/**
 * 用户角色分配请求DTO
 */
@Data
public class UserRoleAssignRequest {

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
}
