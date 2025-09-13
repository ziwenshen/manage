package com.server.manage.dto.menu;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class MenuResponse {
	private Long id;
	private String menuCode;
	private String name;
	private String module;
	private Integer nodeType;
	private Integer sortOrder;
	private LocalDateTime createdAt;
	private Long parentId;
	private List<MenuResponse> children = new ArrayList<>();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getMenuCode() { return menuCode; }
	public void setMenuCode(String menuCode) { this.menuCode = menuCode; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getModule() { return module; }
	public void setModule(String module) { this.module = module; }
	public Integer getNodeType() { return nodeType; }
	public void setNodeType(Integer nodeType) { this.nodeType = nodeType; }
	public Integer getSortOrder() { return sortOrder; }
	public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public Long getParentId() { return parentId; }
	public void setParentId(Long parentId) { this.parentId = parentId; }
	public List<MenuResponse> getChildren() { return children; }
	public void setChildren(List<MenuResponse> children) { this.children = children; }
}
