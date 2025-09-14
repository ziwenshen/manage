# 角色管理接口文档

**基础URL**: `http://127.0.0.1:8080/api`

## 接口概述

角色管理模块提供角色的增删改查以及权限分配功能。所有接口都需要相应的权限验证。

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

## 角色数据模型

### Role 对象

```json
{
  "id": 1,
  "name": "系统管理员",
  "code": "ROLE_ADMIN",
  "description": "系统内置管理员",
  "createdAt": "2025-09-08T09:36:06"
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 否 | 角色ID（自动生成） |
| name | String | 是 | 角色名称 |
| code | String | 是 | 角色编码（唯一） |
| description | String | 否 | 角色描述 |
| createdAt | LocalDateTime | 否 | 创建时间（自动生成） |

---

## 接口列表

### 1. 分页查询角色列表

**接口地址**: `GET /permissions/roles/list`

**权限要求**: `menu:role:view`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**查询参数**:

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| name | String | 否 | - | 角色名称模糊查询 |
| code | String | 否 | - | 角色编码模糊查询 |
| page | Integer | 否 | 1 | 页码，从1开始 |
| size | Integer | 否 | 10 | 每页大小 |

**请求示例**:
```bash
# 1. 查询所有角色（默认分页）
GET /permissions/roles/list

# 2. 按名称模糊查询
GET /permissions/roles/list?name=管理员

# 3. 按编码模糊查询
GET /permissions/roles/list?code=ADMIN

# 4. 组合查询 + 分页
GET /permissions/roles/list?name=管理员&code=ADMIN&page=2&size=5
```

**成功响应** (HTTP 200):
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {
    "data": [
      {
        "id": 1,
        "name": "系统管理员",
        "code": "ROLE_ADMIN",
        "description": "系统内置管理员角色",
        "createdAt": "2025-09-08T09:36:06",
        "updatedAt": "2025-09-08T09:36:06"
      },
      {
        "id": 2,
        "name": "普通用户",
        "code": "ROLE_USER",
        "description": "普通用户角色",
        "createdAt": "2025-09-08T10:15:30"
      }
    ],
    "total": 25,
    "page": 1,
    "size": 10,
    "totalPages": 3
  }
}
```

**权限不足响应** (HTTP 403):
```json
{
  "success": false,
  "code": 500,
  "message": "权限不足，无法执行此操作",
  "data": null
}
```

**响应字段说明**:

*外层响应结构*:
| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 请求是否成功 |
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Object | 分页数据对象 |

*分页数据结构 (data字段)*:
| 字段 | 类型 | 说明 |
|------|------|------|
| data | Array | 角色列表数据 |
| total | Long | 总记录数 |
| page | Integer | 当前页码 |
| size | Integer | 每页大小 |
| totalPages | Integer | 总页数 |

*角色对象结构 (data.data数组中的元素)*:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 角色ID |
| name | String | 角色名称 |
| code | String | 角色编码 |
| description | String | 角色描述 |
| createdAt | DateTime | 创建时间 |

**查询逻辑说明**:
- **模糊查询**: `name`和`code`参数都支持模糊匹配（LIKE '%关键词%'）
- **分页计算**: offset = (page - 1) × size，totalPages = Math.ceil(total ÷ size)
- **排序**: 按创建时间倒序排列（最新创建的在前）

---

### 2. 创建角色

**接口地址**: `POST /permissions/roles/create`

**权限要求**: `menu:role:add`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "name": "测试角色",
  "code": "ROLE_TEST",
  "description": "测试用角色"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 角色名称 |
| code | String | 是 | 角色编码（唯一标识） |
| description | String | 否 | 角色描述 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "角色创建成功",
  "data": null
}
```

---

### 3. 更新角色

**接口地址**: `PUT /permissions/roles/update`

**权限要求**: `menu:role:edit`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
  "id": 1,
  "name": "更新后的角色名",
  "code": "ROLE_UPDATED",
  "description": "更新后的描述"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 角色ID |
| name | String | 是 | 角色名称 |
| code | String | 是 | 角色编码 |
| description | String | 否 | 角色描述 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "角色更新成功",
  "data": null
}
```

---

### 4. 删除角色

**接口地址**: `DELETE /permissions/roles/delete/{id}`

**权限要求**: `menu:role:delete`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 角色ID |

**请求示例**:
```
DELETE /permissions/roles/delete/1
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "角色删除成功",
  "data": null
}
```

---

### 5. 获取角色权限

**接口地址**: `GET /permissions/roles/{roleId}/permissions`

**权限要求**: `menu:role:viewpermission`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色ID |

**请求示例**:
```
GET /permissions/roles/1/permissions
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": [
    "menu:user:view",
    "menu:user:add",
    "menu:user:edit",
    "menu:user:delete",
    "menu:role:view",
    "menu:role:add",
    "menu:role:edit",
    "menu:role:delete"
  ]
}
```

---

### 6. 分配角色权限

**接口地址**: `POST /permissions/roles/{roleId}/permissions`

**权限要求**: `menu:role:assignpermission`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色ID |

**请求参数**:
```json
[1, 2, 3, 4, 5]
```

**参数说明**:
- 请求体为权限ID数组
- 数组中的每个元素都是权限的ID（Long类型）

**请求示例**:
```
POST /permissions/roles/1/permissions
Content-Type: application/json

[1, 2, 3, 4, 5, 6, 7, 8]
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "角色权限分配成功",
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
| 404 | 404 | 资源不存在 | 角色ID不存在 |
| 500 | 500 | 服务器内部错误 | 数据库连接失败、系统异常 |

### 常见错误响应示例

**参数错误** (HTTP 400):
```json
{
  "success": false,
  "code": 400,
  "message": "请求参数错误：角色名称不能为空",
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
  "message": "角色不存在",
  "data": null
}
```

## 权限说明

角色管理接口需要以下权限：

| 权限码 | 说明 |
|--------|------|
| menu:role:view | 查看角色信息 |
| menu:role:add | 创建角色 |
| menu:role:edit | 编辑角色 |
| menu:role:delete | 删除角色 |
| menu:role:viewpermission | 查看角色权限 |
| menu:role:assignpermission | 分配角色权限 |

## 注意事项

1. **认证要求**: 除了权限验证外，所有接口都需要有效的JWT Token
2. **权限验证**: 每个接口都会验证用户是否具有相应的权限
3. **数据验证**: 创建和更新角色时，角色编码必须唯一
4. **逻辑删除**: 删除角色采用逻辑删除，不会物理删除数据
5. **权限分配**: 分配权限时会覆盖角色原有的所有权限

## 使用示例

### 完整的角色管理流程

```bash
# 1. 分页查询角色列表
curl -X GET "http://127.0.0.1:8080/api/permissions/roles/list?name=管理员&page=1&size=10" \
  -H "Authorization: Bearer {your_token}"

# 2. 创建新角色
curl -X POST "http://127.0.0.1:8080/api/permissions/roles/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "name": "编辑员",
    "code": "ROLE_EDITOR",
    "description": "内容编辑员角色"
  }'

# 3. 为角色分配权限
curl -X POST "http://127.0.0.1:8080/api/permissions/roles/1/permissions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '[1, 2, 3, 4]'

# 4. 查看角色权限
curl -X GET "http://127.0.0.1:8080/api/permissions/roles/1/permissions" \
  -H "Authorization: Bearer {your_token}"

# 5. 更新角色信息
curl -X PUT "http://127.0.0.1:8080/api/permissions/roles/update" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "id": 1,
    "name": "高级编辑员",
    "code": "ROLE_SENIOR_EDITOR",
    "description": "高级内容编辑员角色"
  }'

# 6. 删除角色
curl -X DELETE "http://127.0.0.1:8080/api/permissions/roles/delete/1" \
  -H "Authorization: Bearer {your_token}"
```

### JavaScript 示例

```javascript
// 基础配置
const API_BASE = 'http://127.0.0.1:8080/api';

// 获取JWT Token（根据实际情况调整）
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

// 分页查询角色列表
async function getRoles(queryParams = {}) {
  const params = new URLSearchParams(queryParams).toString();
  const url = params ? `/permissions/roles/list?${params}` : '/permissions/roles/list';
  return await apiRequest(url);
}

// 创建角色
async function createRole(roleData) {
  return await apiRequest('/permissions/roles/create', {
    method: 'POST',
    body: JSON.stringify(roleData)
  });
}

// 更新角色
async function updateRole(roleData) {
  return await apiRequest('/permissions/roles/update', {
    method: 'PUT',
    body: JSON.stringify(roleData)
  });
}

// 删除角色
async function deleteRole(roleId) {
  return await apiRequest(`/permissions/roles/delete/${roleId}`, {
    method: 'DELETE'
  });
}

// 获取角色权限
async function getRolePermissions(roleId) {
  return await apiRequest(`/permissions/roles/${roleId}/permissions`);
}

// 分配角色权限
async function assignRolePermissions(roleId, permissionIds) {
  return await apiRequest(`/permissions/roles/${roleId}/permissions`, {
    method: 'POST',
    body: JSON.stringify(permissionIds)
  });
}

// 使用示例
async function example() {
  try {
    // 创建角色
    const createResult = await createRole({
      name: '测试角色',
      code: 'ROLE_TEST',
      description: '这是一个测试角色'
    });
    console.log('创建角色结果:', createResult);

    // 分页查询角色列表
    const roles = await getRoles({ name: '管理员', page: 1, size: 10 });
    console.log('角色列表:', roles);

    // 分配权限
    const assignResult = await assignRolePermissions(1, [1, 2, 3, 4]);
    console.log('权限分配结果:', assignResult);

  } catch (error) {
    console.error('操作失败:', error);
  }
}

// 前端分页组件使用示例
async function loadRoleTable(filters = {}) {
  const params = {
    page: filters.page || 1,
    size: filters.size || 10,
    ...(filters.name && { name: filters.name }),
    ...(filters.code && { code: filters.code })
  };

  const result = await getRoles(params);

  if (result.success) {
    const { data, total, page, size, totalPages } = result.data;

    // 更新表格数据
    updateTable(data);

    // 更新分页信息
    updatePagination({
      current: page,
      pageSize: size,
      total: total,
      totalPages: totalPages
    });

    console.log(`共找到 ${total} 个角色，当前第 ${page} 页，共 ${totalPages} 页`);
  } else {
    console.error('查询失败:', result.message);
  }
}

// 搜索功能示例
function handleSearch(searchForm) {
  const filters = {
    name: searchForm.name,
    code: searchForm.code,
    page: 1, // 搜索时重置到第一页
    size: 10
  };

  loadRoleTable(filters);
}

// 分页切换示例
function handlePageChange(page, pageSize) {
  const currentFilters = getCurrentFilters(); // 获取当前搜索条件

  loadRoleTable({
    ...currentFilters,
    page: page,
    size: pageSize
  });
}
```

---

## 更新日志

### v1.0.0 (2025-09-14)
- 初始版本发布
- 支持角色的基本CRUD操作
- 支持角色权限管理
- 集成权限验证系统
