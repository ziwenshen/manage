# 权限管理接口文档

**基础URL**: `http://127.0.0.1:8080/api`

## 接口概述

权限管理模块提供权限的增删改查功能。权限是系统中最细粒度的访问控制单元，用于控制用户对特定功能的访问权限。

## 通用响应格式

所有接口都返回统一的JSON格式：

```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| success | boolean | 请求是否成功 |
| code | int | 状态码 |
| message | string | 响应消息 |
| data | object | 响应数据 |

### 权限不足响应

```json
{
  "success": false,
  "code": 500,
  "message": "权限不足，无法执行此操作",
  "data": null
}
```

## 权限数据模型

### Permission 对象

```json
{
  "id": 1,
  "code": "menu:user:view",
  "name": "查看用户",
  "module": "user",
  "menuCode": "user",
  "action": "view",
  "url": "/users",
  "createdAt": "2025-09-08T09:36:06"
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 否 | 权限ID（自动生成） |
| code | String | 是 | 权限标识码，格式：module:menuCode:action |
| name | String | 是 | 权限名称（中文描述） |
| module | String | 是 | 业务模块标识 |
| menuCode | String | 是 | 菜单编码 |
| action | String | 是 | 操作类型（view、add、edit、delete等） |
| url | String | 否 | 对应的URL路径 |
| createdAt | LocalDateTime | 否 | 创建时间（自动生成） |

### 权限编码规范

权限编码采用三段式结构：`module:menuCode:action`

**示例**:
- `menu:user:view` - 用户模块查看权限
- `menu:user:add` - 用户模块新增权限
- `menu:user:edit` - 用户模块编辑权限
- `menu:user:delete` - 用户模块删除权限
- `menu:role:view` - 角色模块查看权限
- `menu:system:view` - 系统模块查看权限

---

## 接口列表

### 1. 获取权限列表

**接口地址**: `GET /permissions/perm/list`

**权限要求**: `menu:permission:view`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**请求参数**: 无

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "code": "menu:user:view",
      "name": "查看用户",
      "module": "user",
      "menuCode": "user",
      "action": "view",
      "url": "/users",
      "createdAt": "2025-09-08T09:36:06"
    },
    {
      "id": 2,
      "code": "menu:user:add",
      "name": "新增用户",
      "module": "user",
      "menuCode": "user",
      "action": "add",
      "url": "/users/add",
      "createdAt": "2025-09-08T09:36:06"
    }
  ]
}
```

---

### 2. 创建权限

**接口地址**: `POST /permissions/perm/create`

**权限要求**: `menu:permission:add`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "code": "menu:order:view",
  "name": "查看订单",
  "module": "order",
  "menuCode": "order",
  "action": "view",
  "url": "/orders"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 权限标识码，格式：module:menuCode:action |
| name | String | 是 | 权限名称（中文描述） |
| module | String | 是 | 业务模块标识 |
| menuCode | String | 是 | 菜单编码 |
| action | String | 是 | 操作类型 |
| url | String | 否 | 对应的URL路径 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限创建成功",
  "data": null
}
```

---

### 3. 更新权限

**接口地址**: `PUT /permissions/perm/update`

**权限要求**: `menu:permission:edit`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "id": 1,
  "code": "menu:order:view",
  "name": "查看订单列表",
  "module": "order",
  "menuCode": "order",
  "action": "view",
  "url": "/orders/list"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 权限ID |
| code | String | 是 | 权限标识码 |
| name | String | 是 | 权限名称 |
| module | String | 是 | 业务模块标识 |
| menuCode | String | 是 | 菜单编码 |
| action | String | 是 | 操作类型 |
| url | String | 否 | 对应的URL路径 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限更新成功",
  "data": null
}
```

---

### 4. 删除权限

**接口地址**: `DELETE /permissions/perm/delete/{id}`

**权限要求**: `menu:permission:delete`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 权限ID |

**请求示例**:
```
DELETE /permissions/perm/delete/1
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限删除成功",
  "data": null
}
```

**注意事项**:
- 删除权限前会自动清理相关的角色权限关联和用户权限关联
- 删除操作会清理权限缓存，确保权限变更立即生效

---

### 5. 获取菜单权限树

**接口地址**: `GET /permissions/perm/menu-tree`

**权限要求**: `menu:permission:view`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**请求参数**: 无

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parentId": null,
      "menuCode": "system",
      "name": "系统管理",
      "module": "system",
      "nodeType": 1,
      "sortOrder": 1,
      "createdAt": "2025-09-08T09:36:06",
      "permissions": [],
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "menuCode": "user",
          "name": "用户管理",
          "module": "user",
          "nodeType": 2,
          "sortOrder": 1,
          "createdAt": "2025-09-08T09:36:06",
          "permissions": [
            {
              "id": 1,
              "code": "menu:user:view",
              "name": "查看用户",
              "action": "view",
              "url": "/users",
              "createdAt": "2025-09-08T09:36:06"
            },
            {
              "id": 2,
              "code": "menu:user:add",
              "name": "新增用户",
              "action": "add",
              "url": "/users/add",
              "createdAt": "2025-09-08T09:36:06"
            },
            {
              "id": 3,
              "code": "menu:user:edit",
              "name": "编辑用户",
              "action": "edit",
              "url": "/users/edit",
              "createdAt": "2025-09-08T09:36:06"
            },
            {
              "id": 4,
              "code": "menu:user:delete",
              "name": "删除用户",
              "action": "delete",
              "url": "/users/delete",
              "createdAt": "2025-09-08T09:36:06"
            }
          ],
          "children": []
        },
        {
          "id": 3,
          "parentId": 1,
          "menuCode": "role",
          "name": "角色管理",
          "module": "role",
          "nodeType": 2,
          "sortOrder": 2,
          "createdAt": "2025-09-08T09:36:06",
          "permissions": [
            {
              "id": 5,
              "code": "menu:role:view",
              "name": "查看角色",
              "action": "view",
              "url": "/roles",
              "createdAt": "2025-09-08T09:36:06"
            },
            {
              "id": 6,
              "code": "menu:role:add",
              "name": "新增角色",
              "action": "add",
              "url": "/roles/add",
              "createdAt": "2025-09-08T09:36:06"
            }
          ],
          "children": []
        }
      ]
    }
  ]
}
```

**响应字段说明**:

*菜单对象结构*:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜单ID |
| parentId | Long | 父级菜单ID |
| menuCode | String | 菜单编码 |
| name | String | 菜单名称 |
| module | String | 业务模块标识 |
| nodeType | Integer | 节点类型：1=文件夹，2=页面，3=按钮 |
| sortOrder | Integer | 排序序号 |
| createdAt | DateTime | 创建时间 |
| permissions | Array | 该菜单下的权限列表 |
| children | Array | 子菜单列表 |

*权限对象结构 (permissions数组中的元素)*:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 权限ID |
| code | String | 权限编码 |
| name | String | 权限名称 |
| action | String | 操作类型 |
| url | String | URL路径 |
| createdAt | DateTime | 创建时间 |

**使用场景**:
- 角色分配权限时的权限选择界面
- 权限管理界面的树形展示
- 前端菜单权限控制的数据源

**特点**:
- 返回完整的菜单树结构，包含所有层级关系
- 每个菜单节点包含其下的所有权限信息
- 按照sortOrder和id进行排序
- 支持文件夹、页面、按钮三种节点类型

---

## 错误码说明

| HTTP状态码 | 响应码 | 说明 | 示例场景 |
|-----------|--------|------|----------|
| 200 | 200 | 请求成功 | 正常的API调用 |
| 400 | 400 | 请求参数错误 | 缺少必填参数、参数格式错误 |
| 401 | 401 | 未认证 | JWT Token无效或过期 |
| 403 | 500 | 权限不足 | 用户没有相应的操作权限 |
| 404 | 404 | 资源不存在 | 权限ID不存在 |
| 500 | 500 | 服务器内部错误 | 数据库连接失败、系统异常 |

### 常见错误响应示例

**参数错误** (HTTP 400):
```json
{
  "success": false,
  "code": 400,
  "message": "请求参数错误：权限编码不能为空",
  "data": null
}
```

**权限编码重复** (HTTP 400):
```json
{
  "success": false,
  "code": 400,
  "message": "权限编码已存在：menu:user:view",
  "data": null
}
```

**未认证** (HTTP 401):
```json
{
  "success": false,
  "code": 401,
  "message": "JWT Token无效或已过期",
  "data": null
}
```

**权限不足** (HTTP 403):
```json
{
  "success": false,
  "code": 500,
  "message": "权限不足，无法执行此操作",
  "data": null
}
```

**资源不存在** (HTTP 404):
```json
{
  "success": false,
  "code": 404,
  "message": "权限不存在",
  "data": null
}
```

## 权限说明

权限管理接口需要以下权限：

| 权限码 | 说明 |
|--------|------|
| menu:permission:view | 查看权限信息 |
| menu:permission:add | 创建权限 |
| menu:permission:edit | 编辑权限 |
| menu:permission:delete | 删除权限 |

## 业务规则

### 权限编码规范

1. **格式要求**: `module:menuCode:action`
2. **命名规范**:
   - module: 业务模块名称，如 user、role、order 等
   - menuCode: 菜单编码，通常与module相同或为具体页面标识
   - action: 操作类型，标准操作包括：
     - `view`: 查看/列表
     - `add`: 新增/创建
     - `edit`: 编辑/修改
     - `delete`: 删除
     - `export`: 导出
     - `import`: 导入

3. **示例权限编码**:
   ```
   menu:user:view          # 用户查看权限
   menu:user:add           # 用户新增权限
   menu:user:edit          # 用户编辑权限
   menu:user:delete        # 用户删除权限
   menu:role:view          # 角色查看权限
   menu:role:assignpermission  # 角色分配权限
   menu:order:export       # 订单导出权限
   ```

### 权限层级关系

- **模块级权限**: 控制对整个业务模块的访问
- **功能级权限**: 控制对具体功能的访问
- **操作级权限**: 控制对具体操作的访问

### 缓存机制

- 权限数据会被缓存到Redis中以提高性能
- 权限变更（增删改）会自动清理相关缓存
- 用户权限缓存会在权限变更后自动刷新

## 使用示例

### 完整的权限管理流程

```bash
# 1. 获取权限列表
curl -X GET "http://127.0.0.1:8080/api/permissions/perm/list" \
  -H "Authorization: Bearer {your_token}"

# 2. 创建新权限
curl -X POST "http://127.0.0.1:8080/api/permissions/perm/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "code": "menu:product:view",
    "name": "查看商品",
    "module": "product",
    "menuCode": "product",
    "action": "view",
    "url": "/products"
  }'

# 3. 更新权限信息
curl -X PUT "http://127.0.0.1:8080/api/permissions/perm/update" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "id": 1,
    "code": "menu:product:view",
    "name": "查看商品列表",
    "module": "product",
    "menuCode": "product",
    "action": "view",
    "url": "/products/list"
  }'

# 4. 删除权限
curl -X DELETE "http://127.0.0.1:8080/api/permissions/perm/delete/1" \
  -H "Authorization: Bearer {your_token}"

# 5. 获取菜单权限树（用于角色分配权限）
curl -X GET "http://127.0.0.1:8080/api/permissions/perm/menu-tree" \
  -H "Authorization: Bearer {your_token}"
```

### JavaScript 示例

```javascript
// 基础配置
const API_BASE = 'http://127.0.0.1:8080/api';

// 获取JWT Token
function getToken() {
  return localStorage.getItem('jwt_token') || 'your_jwt_token_here';
}

// 通用请求函数
async function apiRequest(url, options = {}) {
  const response = await fetch(`${API_BASE}${url}`, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`,
      ...options.headers
    },
    ...options
  });
  return response.json();
}

// 获取权限列表
async function getPermissions() {
  return await apiRequest('/permissions/perm/list');
}

// 创建权限
async function createPermission(permissionData) {
  return await apiRequest('/permissions/perm/create', {
    method: 'POST',
    body: JSON.stringify(permissionData)
  });
}

// 更新权限
async function updatePermission(permissionData) {
  return await apiRequest('/permissions/perm/update', {
    method: 'PUT',
    body: JSON.stringify(permissionData)
  });
}

// 删除权限
async function deletePermission(permissionId) {
  return await apiRequest(`/permissions/perm/delete/${permissionId}`, {
    method: 'DELETE'
  });
}

// 获取菜单权限树
async function getMenuPermissionTree() {
  return await apiRequest('/permissions/perm/menu-tree');
}

// 使用示例
async function example() {
  try {
    // 创建权限
    const createResult = await createPermission({
      code: 'menu:inventory:view',
      name: '查看库存',
      module: 'inventory',
      menuCode: 'inventory',
      action: 'view',
      url: '/inventory'
    });
    console.log('创建权限结果:', createResult);

    // 获取权限列表
    const permissions = await getPermissions();
    console.log('权限列表:', permissions);

    // 更新权限
    const updateResult = await updatePermission({
      id: 1,
      code: 'menu:inventory:view',
      name: '查看库存管理',
      module: 'inventory',
      menuCode: 'inventory',
      action: 'view',
      url: '/inventory/list'
    });
    console.log('更新权限结果:', updateResult);

  } catch (error) {
    console.error('操作失败:', error);
  }
}

// 权限管理组件示例
class PermissionManager {
  constructor() {
    this.permissions = [];
  }

  // 加载权限列表
  async loadPermissions() {
    try {
      const result = await getPermissions();
      if (result.success) {
        this.permissions = result.data;
        this.renderPermissionTable();
      } else {
        console.error('加载权限失败:', result.message);
      }
    } catch (error) {
      console.error('加载权限异常:', error);
    }
  }

  // 渲染权限表格
  renderPermissionTable() {
    // 实现权限表格渲染逻辑
    console.log('渲染权限表格:', this.permissions);
  }

  // 添加权限
  async addPermission(formData) {
    try {
      const result = await createPermission(formData);
      if (result.success) {
        console.log('权限创建成功');
        await this.loadPermissions(); // 重新加载列表
      } else {
        console.error('创建权限失败:', result.message);
      }
    } catch (error) {
      console.error('创建权限异常:', error);
    }
  }

  // 编辑权限
  async editPermission(permissionData) {
    try {
      const result = await updatePermission(permissionData);
      if (result.success) {
        console.log('权限更新成功');
        await this.loadPermissions(); // 重新加载列表
      } else {
        console.error('更新权限失败:', result.message);
      }
    } catch (error) {
      console.error('更新权限异常:', error);
    }
  }

  // 删除权限
  async removePermission(permissionId) {
    if (confirm('确定要删除这个权限吗？')) {
      try {
        const result = await deletePermission(permissionId);
        if (result.success) {
          console.log('权限删除成功');
          await this.loadPermissions(); // 重新加载列表
        } else {
          console.error('删除权限失败:', result.message);
        }
      } catch (error) {
        console.error('删除权限异常:', error);
      }
    }
  }
}

// 初始化权限管理器
const permissionManager = new PermissionManager();
permissionManager.loadPermissions();

// 角色权限分配组件示例
class RolePermissionAssigner {
  constructor() {
    this.menuTree = [];
    this.selectedPermissions = new Set();
  }

  // 加载菜单权限树
  async loadMenuPermissionTree() {
    try {
      const result = await getMenuPermissionTree();
      if (result.success) {
        this.menuTree = result.data;
        this.renderPermissionTree();
      } else {
        console.error('加载菜单权限树失败:', result.message);
      }
    } catch (error) {
      console.error('加载菜单权限树异常:', error);
    }
  }

  // 渲染权限树
  renderPermissionTree() {
    const container = document.getElementById('permission-tree');
    container.innerHTML = this.buildTreeHTML(this.menuTree);
    this.bindEvents();
  }

  // 构建树形HTML
  buildTreeHTML(nodes, level = 0) {
    let html = '';
    for (const node of nodes) {
      const indent = '  '.repeat(level);
      const nodeTypeText = this.getNodeTypeText(node.nodeType);

      html += `
        <div class="tree-node" style="margin-left: ${level * 20}px;">
          <div class="menu-item">
            <span class="node-type">[${nodeTypeText}]</span>
            <span class="menu-name">${node.name}</span>
            <span class="menu-code">(${node.menuCode})</span>
          </div>
      `;

      // 渲染权限复选框
      if (node.permissions && node.permissions.length > 0) {
        html += '<div class="permissions">';
        for (const permission of node.permissions) {
          const checked = this.selectedPermissions.has(permission.id) ? 'checked' : '';
          html += `
            <label class="permission-item">
              <input type="checkbox" value="${permission.id}" ${checked}
                     data-code="${permission.code}" data-name="${permission.name}">
              <span class="permission-name">${permission.name}</span>
              <span class="permission-code">(${permission.code})</span>
            </label>
          `;
        }
        html += '</div>';
      }

      // 递归渲染子节点
      if (node.children && node.children.length > 0) {
        html += this.buildTreeHTML(node.children, level + 1);
      }

      html += '</div>';
    }
    return html;
  }

  // 获取节点类型文本
  getNodeTypeText(nodeType) {
    switch (nodeType) {
      case 1: return '文件夹';
      case 2: return '页面';
      case 3: return '按钮';
      default: return '未知';
    }
  }

  // 绑定事件
  bindEvents() {
    const checkboxes = document.querySelectorAll('#permission-tree input[type="checkbox"]');
    checkboxes.forEach(checkbox => {
      checkbox.addEventListener('change', (e) => {
        const permissionId = parseInt(e.target.value);
        if (e.target.checked) {
          this.selectedPermissions.add(permissionId);
        } else {
          this.selectedPermissions.delete(permissionId);
        }
        this.updateSelectedCount();
      });
    });
  }

  // 更新选中数量显示
  updateSelectedCount() {
    const countElement = document.getElementById('selected-count');
    if (countElement) {
      countElement.textContent = `已选择 ${this.selectedPermissions.size} 个权限`;
    }
  }

  // 获取选中的权限ID列表
  getSelectedPermissionIds() {
    return Array.from(this.selectedPermissions);
  }

  // 设置选中的权限（用于编辑角色时回显）
  setSelectedPermissions(permissionIds) {
    this.selectedPermissions = new Set(permissionIds);
    this.renderPermissionTree();
  }

  // 全选/取消全选
  toggleSelectAll() {
    const allPermissions = this.getAllPermissionIds(this.menuTree);
    if (this.selectedPermissions.size === allPermissions.length) {
      // 当前全选，执行取消全选
      this.selectedPermissions.clear();
    } else {
      // 执行全选
      allPermissions.forEach(id => this.selectedPermissions.add(id));
    }
    this.renderPermissionTree();
  }

  // 获取所有权限ID
  getAllPermissionIds(nodes) {
    let ids = [];
    for (const node of nodes) {
      if (node.permissions) {
        ids.push(...node.permissions.map(p => p.id));
      }
      if (node.children) {
        ids.push(...this.getAllPermissionIds(node.children));
      }
    }
    return ids;
  }
}

// 使用示例
const rolePermissionAssigner = new RolePermissionAssigner();

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', () => {
  rolePermissionAssigner.loadMenuPermissionTree();
});

// 保存角色权限
async function saveRolePermissions(roleId) {
  const selectedPermissions = rolePermissionAssigner.getSelectedPermissionIds();

  try {
    const response = await fetch(`/api/permissions/roles/${roleId}/permissions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        permissionIds: selectedPermissions
      })
    });

    const result = await response.json();
    if (result.success) {
      alert('权限分配成功！');
    } else {
      alert('权限分配失败：' + result.message);
    }
  } catch (error) {
    console.error('保存权限失败:', error);
    alert('保存权限失败，请重试');
  }
}
```

## 注意事项

1. **认证要求**: 所有接口都需要有效的JWT Token
2. **权限验证**: 每个接口都会验证用户是否具有相应的权限
3. **权限编码唯一性**: 权限编码在系统中必须唯一
4. **缓存一致性**: 权限变更会自动清理相关缓存，确保数据一致性
5. **级联删除**: 删除权限时会自动清理相关的角色权限关联
6. **权限编码规范**: 建议遵循 `module:menuCode:action` 的命名规范
7. **URL路径**: URL字段为可选，主要用于前端路由控制
8. **操作审计**: 权限的增删改操作建议记录操作日志

## 最佳实践

### 权限设计原则

1. **最小权限原则**: 用户只应拥有完成工作所需的最小权限
2. **权限分离**: 不同模块的权限应该分离，避免权限过于集中
3. **权限继承**: 通过角色继承权限，避免直接给用户分配过多权限
4. **定期审查**: 定期审查和清理不必要的权限

### 权限命名建议

1. **模块命名**: 使用简洁明了的英文单词，如 user、role、order
2. **操作命名**: 使用标准的CRUD操作名称，保持一致性
3. **特殊操作**: 对于特殊操作，使用描述性的名称，如 export、import、approve

### 前端集成

```javascript
// 权限检查工具函数
function hasPermission(requiredPermission) {
  const userPermissions = getUserPermissions(); // 从本地存储或状态管理中获取
  return userPermissions.includes(requiredPermission);
}

// 按钮权限控制
function renderButton(permission, buttonText, onClick) {
  if (hasPermission(permission)) {
    return `<button onclick="${onClick}">${buttonText}</button>`;
  }
  return ''; // 无权限时不显示按钮
}

// 菜单权限控制
function filterMenuByPermissions(menus) {
  return menus.filter(menu => {
    if (menu.permission && !hasPermission(menu.permission)) {
      return false;
    }
    if (menu.children) {
      menu.children = filterMenuByPermissions(menu.children);
    }
    return true;
  });
}
```

---

## 更新日志

### v1.0.0 (2025-09-14)
- 初始版本发布
- 支持权限的基本CRUD操作
- 集成权限验证系统
- 支持权限缓存机制

**示例**:
- `menu:user:view` - 用户模块查看权限
- `menu:user:add` - 用户模块新增权限
- `menu:user:edit` - 用户模块编辑权限
- `menu:user:delete` - 用户模块删除权限
- `menu:role:view` - 角色模块查看权限
- `menu:role:assignpermission` - 角色模块分配权限
- `menu:permission:view` - 权限模块查看权限
- `menu:permission:add` - 权限模块新增权限
- `menu:permission:edit` - 权限模块编辑权限
- `menu:permission:delete` - 权限模块删除权限

---

## 接口列表

### 1. 获取权限列表

**接口地址**: `GET /permissions/perm/list`

**权限要求**: `menu:permission:view`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**请求参数**: 无

**请求示例**:
```bash
curl -X GET "http://127.0.0.1:8080/api/permissions/perm/list" \
  -H "Authorization: Bearer {your_token}"
```

**成功响应** (HTTP 200):
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "code": "menu:user:view",
      "name": "查看用户",
      "module": "user",
      "menuCode": "user",
      "action": "view",
      "url": "/users",
      "createdAt": "2025-09-08T09:36:06"
    },
    {
      "id": 2,
      "code": "menu:user:add",
      "name": "新增用户",
      "module": "user",
      "menuCode": "user",
      "action": "add",
      "url": "/users/add",
      "createdAt": "2025-09-08T09:36:06"
    }
  ]
}
```

---

### 2. 创建权限

**接口地址**: `POST /permissions/perm/create`

**权限要求**: `menu:permission:add`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "code": "menu:product:view",
  "name": "查看产品",
  "module": "product",
  "menuCode": "product",
  "action": "view",
  "url": "/products"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 权限标识码（唯一） |
| name | String | 是 | 权限名称 |
| module | String | 是 | 业务模块标识 |
| menuCode | String | 是 | 菜单编码 |
| action | String | 是 | 操作类型 |
| url | String | 否 | 对应的URL路径 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限创建成功",
  "data": null
}
```

---

### 3. 更新权限

**接口地址**: `PUT /permissions/perm/update`

**权限要求**: `menu:permission:edit`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "id": 1,
  "code": "menu:product:view",
  "name": "查看产品列表",
  "module": "product",
  "menuCode": "product",
  "action": "view",
  "url": "/products/list"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 权限ID |
| code | String | 是 | 权限标识码 |
| name | String | 是 | 权限名称 |
| module | String | 是 | 业务模块标识 |
| menuCode | String | 是 | 菜单编码 |
| action | String | 是 | 操作类型 |
| url | String | 否 | 对应的URL路径 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限更新成功",
  "data": null
}
```

---

### 4. 删除权限

**接口地址**: `DELETE /permissions/perm/delete/{id}`

**权限要求**: `menu:permission:delete`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 权限ID |

**请求示例**:
```
DELETE /permissions/perm/delete/1
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "权限删除成功",
  "data": null
}
```

---

## 错误码说明

| HTTP状态码 | 响应码 | 说明 | 示例场景 |
|-----------|--------|------|----------|
| 200 | 200 | 请求成功 | 正常的API调用 |
| 400 | 400 | 请求参数错误 | 缺少必填参数、参数格式错误 |
| 401 | 401 | 未认证 | JWT Token无效或过期 |
| 403 | 500 | 权限不足 | 用户没有相应的操作权限 |
| 404 | 404 | 资源不存在 | 权限ID不存在 |
| 500 | 500 | 服务器内部错误 | 数据库连接失败、系统异常 |

### 常见错误响应示例

**参数错误** (HTTP 400):
```json
{
  "success": false,
  "code": 400,
  "message": "请求参数错误：权限编码不能为空",
  "data": null
}
```

**权限编码重复** (HTTP 400):
```json
{
  "success": false,
  "code": 400,
  "message": "权限编码已存在",
  "data": null
}
```

**资源不存在** (HTTP 404):
```json
{
  "success": false,
  "code": 404,
  "message": "权限不存在",
  "data": null
}
```

## 使用示例

### 完整的权限管理流程

```bash
# 1. 获取权限列表
curl -X GET "http://127.0.0.1:8080/api/permissions/perm/list" \
  -H "Authorization: Bearer {your_token}"

# 2. 创建新权限
curl -X POST "http://127.0.0.1:8080/api/permissions/perm/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "code": "menu:order:view",
    "name": "查看订单",
    "module": "order",
    "menuCode": "order",
    "action": "view",
    "url": "/orders"
  }'

# 3. 更新权限信息
curl -X PUT "http://127.0.0.1:8080/api/permissions/perm/update" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "id": 1,
    "code": "menu:order:view",
    "name": "查看订单列表",
    "module": "order",
    "menuCode": "order",
    "action": "view",
    "url": "/orders/list"
  }'

# 4. 删除权限
curl -X DELETE "http://127.0.0.1:8080/api/permissions/perm/delete/1" \
  -H "Authorization: Bearer {your_token}"
```

### JavaScript 示例

```javascript
// 基础配置
const API_BASE = 'http://127.0.0.1:8080/api';

// 获取JWT Token
function getToken() {
  return localStorage.getItem('jwt_token') || 'your_jwt_token_here';
}

// 通用请求函数
async function apiRequest(url, options = {}) {
  const response = await fetch(`${API_BASE}${url}`, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`,
      ...options.headers
    },
    ...options
  });
  return response.json();
}

// 获取权限列表
async function getPermissions() {
  return await apiRequest('/permissions/perm/list');
}

// 创建权限
async function createPermission(permissionData) {
  return await apiRequest('/permissions/perm/create', {
    method: 'POST',
    body: JSON.stringify(permissionData)
  });
}

// 更新权限
async function updatePermission(permissionData) {
  return await apiRequest('/permissions/perm/update', {
    method: 'PUT',
    body: JSON.stringify(permissionData)
  });
}

// 删除权限
async function deletePermission(permissionId) {
  return await apiRequest(`/permissions/perm/delete/${permissionId}`, {
    method: 'DELETE'
  });
}

// 使用示例
async function example() {
  try {
    // 创建权限
    const createResult = await createPermission({
      code: 'menu:product:view',
      name: '查看产品',
      module: 'product',
      menuCode: 'product',
      action: 'view',
      url: '/products'
    });
    console.log('创建权限结果:', createResult);

    // 获取权限列表
    const permissions = await getPermissions();
    console.log('权限列表:', permissions);

    // 更新权限
    const updateResult = await updatePermission({
      id: 1,
      code: 'menu:product:view',
      name: '查看产品列表',
      module: 'product',
      menuCode: 'product',
      action: 'view',
      url: '/products/list'
    });
    console.log('更新权限结果:', updateResult);

  } catch (error) {
    console.error('操作失败:', error);
  }
}

// 权限管理表格组件示例
async function loadPermissionTable() {
  const result = await getPermissions();

  if (result.success) {
    const permissions = result.data;

    // 更新表格数据
    updatePermissionTable(permissions);

    console.log(`共找到 ${permissions.length} 个权限`);
  } else {
    console.error('查询失败:', result.message);
  }
}

// 权限编码生成器
function generatePermissionCode(module, menuCode, action) {
  return `menu:${menuCode}:${action}`;
}

// 批量创建权限示例
async function createBatchPermissions(module, menuCode, actions) {
  const results = [];

  for (const action of actions) {
    const permission = {
      code: generatePermissionCode(module, menuCode, action.code),
      name: action.name,
      module: module,
      menuCode: menuCode,
      action: action.code,
      url: action.url
    };

    try {
      const result = await createPermission(permission);
      results.push({ success: true, permission, result });
    } catch (error) {
      results.push({ success: false, permission, error });
    }
  }

  return results;
}

// 使用批量创建
const actions = [
  { code: 'view', name: '查看产品', url: '/products' },
  { code: 'add', name: '新增产品', url: '/products/add' },
  { code: 'edit', name: '编辑产品', url: '/products/edit' },
  { code: 'delete', name: '删除产品', url: '/products/delete' }
];

createBatchPermissions('product', 'product', actions)
  .then(results => console.log('批量创建结果:', results));
```

## 权限设计最佳实践

### 1. 权限编码规范

- **统一格式**: 使用 `module:menuCode:action` 三段式结构
- **命名规范**: 使用小写字母和下划线，避免特殊字符
- **语义清晰**: 编码应该能够清楚表达权限的含义

### 2. 权限粒度控制

- **功能级权限**: 针对具体的业务功能设置权限
- **操作级权限**: 区分查看、新增、编辑、删除等操作
- **数据级权限**: 可以结合数据范围进行更细粒度的控制

### 3. 权限分组管理

```javascript
// 按模块分组的权限示例
const permissionGroups = {
  user: [
    { code: 'menu:user:view', name: '查看用户' },
    { code: 'menu:user:add', name: '新增用户' },
    { code: 'menu:user:edit', name: '编辑用户' },
    { code: 'menu:user:delete', name: '删除用户' }
  ],
  role: [
    { code: 'menu:role:view', name: '查看角色' },
    { code: 'menu:role:add', name: '新增角色' },
    { code: 'menu:role:edit', name: '编辑角色' },
    { code: 'menu:role:delete', name: '删除角色' },
    { code: 'menu:role:assignpermission', name: '分配权限' }
  ],
  permission: [
    { code: 'menu:permission:view', name: '查看权限' },
    { code: 'menu:permission:add', name: '新增权限' },
    { code: 'menu:permission:edit', name: '编辑权限' },
    { code: 'menu:permission:delete', name: '删除权限' }
  ]
};
```

## 注意事项

1. **权限编码唯一性**: 权限编码必须在系统中保持唯一
2. **权限依赖关系**: 删除权限前需要检查是否被角色或用户使用
3. **缓存更新**: 权限变更后需要清理相关缓存，确保权限立即生效
4. **权限继承**: 考虑权限的继承关系，避免权限冗余
5. **审计日志**: 建议记录权限的创建、修改、删除操作日志

## 相关接口

- [角色管理接口](./role-management-api.md) - 角色权限分配
- [用户管理接口](./user-management-api.md) - 用户权限查询
- [菜单管理接口](./menu-management-api.md) - 菜单权限关联

---

## 更新日志

### v1.0.0 (2025-09-14)
- 初始版本发布
- 支持权限的基本CRUD操作
- 提供完整的权限编码规范
- 集成权限验证系统
