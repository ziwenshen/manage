package com.server.manage.dto.permission;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限树形结构响应DTO
 */
@Data
public class PermissionTreeResponse {

    /**
     * 节点ID（菜单ID或权限ID）
     */
    private Long id;

    /**
     * 节点类型：menu=菜单节点，permission=权限节点
     */
    private String nodeType;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点编码（菜单编码或权限编码）
     */
    private String code;

    /**
     * 业务模块标识
     */
    private String module;

    /**
     * 菜单编码（权限节点时使用）
     */
    private String menuCode;

    /**
     * 操作类型（权限节点时使用）
     */
    private String action;



    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 子节点列表
     */
    private List<PermissionTreeResponse> children = new ArrayList<>();

    /**
     * 构造菜单节点
     */
    public static PermissionTreeResponse createMenuNode(Long id, String name, String code, String module, Integer sortOrder, LocalDateTime createdAt) {
        PermissionTreeResponse node = new PermissionTreeResponse();
        node.setId(id);
        node.setNodeType("menu");
        node.setName(name);
        node.setCode(code);
        node.setModule(module);
        node.setSortOrder(sortOrder);
        node.setCreatedAt(createdAt);
        return node;
    }

    /**
     * 构造权限节点
     */
    public static PermissionTreeResponse createPermissionNode(Long id, String name, String code, String module,
                                                            String menuCode, String action, LocalDateTime createdAt) {
        PermissionTreeResponse node = new PermissionTreeResponse();
        node.setId(id);
        node.setNodeType("permission");
        node.setName(name);
        node.setCode(code);
        node.setModule(module);
        node.setMenuCode(menuCode);
        node.setAction(action);
        node.setCreatedAt(createdAt);
        return node;
    }
}
