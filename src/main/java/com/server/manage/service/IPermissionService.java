package com.server.manage.service;

import com.server.manage.dto.permission.PermissionQueryRequest;
import com.server.manage.dto.permission.PermissionResponse;
import com.server.manage.dto.permission.PermissionTreeResponse;
import com.server.manage.model.Permission;

import java.util.List;
import java.util.Set;

public interface IPermissionService {
    /**
     * 获取指定ID的权限实体
     * @param id 权限ID
     * @return Permission 实体或 null
     */
    Permission getById(Long id);

    /**
     * 查询所有权限并返回列表
     * @return 权限列表
     */
    List<Permission> listAll();

    /**
     * 创建权限记录
     * @param permission 权限实体
     * @return 新创建权限的ID
     */
    Long create(Permission permission);

    /**
     * 更新权限信息
     * @param permission 更新后的权限实体（需包含ID）
     */
    void update(Permission permission);

    /**
     * 删除权限
     * @param id 权限ID
     */
    void delete(Long id);

    /**
     * 查询用户有view权限的菜单（文件夹或界面），并组装为树结构
     */
    List<com.server.manage.dto.menu.MenuResponse> getUserMenusWithViewPermission(Long userId);

    /**
     * 按用户ID加载该用户的权限集合，优先从缓存读取
     * @param userId 用户ID
     * @return 权限字符串集合
     */
    Set<String> loadUserPermissions(Long userId);

    /**
     * 刷新用户权限缓存（在权限变更后调用）
     * @param userId 用户ID
     */
    void refreshUserPermissions(Long userId);

    /**
     * 获取菜单权限树，用于角色分配权限选择
     * @return 菜单权限树列表
     */
    List<com.server.manage.dto.menu.MenuPermissionTreeResponse> getMenuPermissionTree();

    /**
     * 分页查询权限列表
     * @param request 查询请求
     * @return 权限列表
     */
    List<PermissionResponse> getPermissionList(PermissionQueryRequest request);

    /**
     * 查询权限总数
     * @param request 查询请求
     * @return 权限总数
     */
    Long getPermissionCount(PermissionQueryRequest request);

    /**
     * 获取权限树形结构（按菜单层级组织）
     * @return 权限树列表
     */
    List<PermissionTreeResponse> getPermissionTree();

    /**
     * 根据权限编码查询权限
     * @param code 权限编码
     * @return 权限实体
     */
    Permission getByCode(String code);
}
