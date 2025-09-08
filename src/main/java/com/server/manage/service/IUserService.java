package com.server.manage.service;

import java.util.List;
import java.util.Set;

public interface IUserService {
    Set<Long> getUserRoles(Long userId);
    void assignUserRoles(Long userId, List<Long> roleIds);
}
