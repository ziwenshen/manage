package com.server.manage.service;

import com.server.manage.model.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    Role getById(Long id);
    List<Role> listAll();
    Long create(Role role);
    void update(Role role);
    void delete(Long id);
    Set<String> getRolePermissions(Long roleId);
    void assignPermissions(Long roleId, java.util.List<Long> permissionIds);
}
