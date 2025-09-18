package com.server.manage.mapper;

import com.server.manage.dto.permission.PermissionQueryRequest;
import com.server.manage.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    Permission selectById(@Param("id") Long id);

    /**
     * 根据一组ID批量查询 Permission
     */
    List<Permission> selectByIds(@Param("ids") java.util.Set<Long> ids);

    List<Permission> selectAll();

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(@Param("id") Long id);

    /**
     * 查询用户有view权限的菜单（文件夹或界面）
     */
    List<java.util.Map<String, Object>> selectUserMenusWithViewPermission(@Param("userId") Long userId);

    /**
     * 分页查询权限列表
     */
    List<Permission> selectPageList(@Param("query") PermissionQueryRequest query,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    /**
     * 查询权限总数
     */
    Long selectCount(@Param("query") PermissionQueryRequest query);

    /**
     * 根据权限编码查询权限
     */
    Permission selectByCode(@Param("code") String code);
}
