package com.server.manage.dto.menu;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long parentId;
    private String menuCode;
    private String name;
    private String module;
    private Integer nodeType;
    private Integer sortOrder;
    private String url;
    private String icon;
    private String path;
    private String meta;
}
