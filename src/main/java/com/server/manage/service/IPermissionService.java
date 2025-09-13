package com.server.manage.service;

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
}
