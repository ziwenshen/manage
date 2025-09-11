# 后端登录接口完整文档

## 接口概述

登录接口已完美升级，现在返回完整的用户信息、权限信息和登录状态信息，满足现代前端应用的所有需求。

## 登录接口

### 请求
- **URL**: `POST /auth/login`
- **Content-Type**: `application/json`

```json
{
    "username": "admin",
    "password": "admin"
}
```

### 完整响应结构

```json
{
    "success": true,
    "code": 200,
    "message": "操作成功",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "userInfo": {
            "userId": 1,
            "username": "admin",
            "enabled": true,
            "createdAt": "2025-01-01T08:00:00",
            "displayName": "admin",
            "avatar": null,
            "email": null
        },
        "permissionInfo": {
            "roleIds": [1, 2],
            "roleNames": ["管理员", "普通用户"],
            "permissions": [
                "user:user-list:view",
                "user:user-list:add",
                "user:user-list:edit",
                "user:user-list:delete",
                "order:order-list:view",
                "order:order-list:export"
            ],
            "menus": null
        },
        "loginStatusInfo": {
            "loginTime": "2025-01-01T10:30:00",
            "loginIp": "192.168.1.100",
            "clientInfo": "Web Client",
            "sessionId": "eyJhbGciOi..."
        }
    }
}
```

## 响应字段说明

### 基本认证信息
- `accessToken`: JWT访问令牌，用于后续API调用
- `tokenType`: 令牌类型，固定为"Bearer"
- `expiresIn`: 令牌过期时间（秒）

### 用户基本信息 (userInfo)
- `userId`: 用户唯一标识
- `username`: 用户名/登录名
- `enabled`: 用户是否启用
- `createdAt`: 用户创建时间
- `displayName`: 显示名称
- `avatar`: 头像URL（扩展字段，可为空）
- `email`: 邮箱（扩展字段，可为空）

### 权限信息 (permissionInfo)
- `roleIds`: 用户角色ID列表
- `roleNames`: 用户角色名称列表
- `permissions`: 用户权限码列表（按钮级权限）
- `menus`: 用户可访问菜单列表（可扩展）

### 登录状态信息 (loginStatusInfo)
- `loginTime`: 当前登录时间
- `loginIp`: 登录来源IP地址
- `clientInfo`: 客户端信息
- `sessionId`: 会话标识（截取的token前缀）

## 前端使用指南

### 1. 基本认证
```javascript
// 保存token
localStorage.setItem('accessToken', response.data.accessToken);

// 设置请求头
axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
```

### 2. 用户信息显示
```javascript
const { userInfo } = response.data;
console.log(`欢迎 ${userInfo.displayName || userInfo.username}`);
```

### 3. 权限控制
```javascript
const { permissionInfo } = response.data;

// 角色判断
const isAdmin = permissionInfo.roleNames.includes('管理员');

// 按钮权限判断
const canAddUser = permissionInfo.permissions.includes('user:user-list:add');
const canDeleteUser = permissionInfo.permissions.includes('user:user-list:delete');

// 动态显示/隐藏按钮
if (canAddUser) {
    showAddButton();
}
```

### 4. 登录状态显示
```javascript
const { loginStatusInfo } = response.data;
console.log(`登录时间: ${loginStatusInfo.loginTime}`);
console.log(`登录IP: ${loginStatusInfo.loginIp}`);
```

## 安全特性

1. **JWT Token**: 无状态认证，支持分布式
2. **Redis会话管理**: 支持单点登录和会话失效
3. **权限缓存**: Redis缓存权限信息，提高性能
4. **客户端IP记录**: 记录登录来源，增强安全审计

## 兼容性说明

- **向下兼容**: 保持了原有LoginResponse的基本结构
- **渐进增强**: 新增的字段可以选择性使用
- **类型安全**: 所有字段都有明确的类型定义

## 错误处理

```json
{
    "success": false,
    "code": 401,
    "message": "用户名或密码错误",
    "data": null
}
```

常见错误码：
- `400`: 请求参数错误
- `401`: 认证失败（用户名或密码错误）
- `500`: 服务器内部错误

## 权限字符串格式

权限采用三段式格式：`{module}:{menuCode}:{action}`

示例：
- `user:user-list:view` - 用户模块-用户列表-查看权限
- `user:user-list:add` - 用户模块-用户列表-新增权限
- `order:order-list:export` - 订单模块-订单列表-导出权限

这样的设计让前端可以精确控制到按钮级别的权限显示。
