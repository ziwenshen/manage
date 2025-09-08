package com.server.manage.service;

import com.server.manage.model.Menu;

import java.util.List;

public interface IMenuService {
    List<Menu> listAll();
    Long create(Menu menu);
    void update(Menu menu);
    void delete(Long id);
}
