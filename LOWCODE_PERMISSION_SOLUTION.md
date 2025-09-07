# 低代码统一接口权限控制解决方案

## 问题场景
前端通过低代码实现表单的增删改查，这些操作都通过统一接口执行，无法使用传统的注解方式进行权限控制。

## 解决方案

### 方案一：请求参数权限映射（推荐）

#### 1.1 统一接口设计
```http
POST /api/lowcode/execute
Content-Type: application/json

{
  "moduleCode": "user_management",     // 模块标识
  "actionCode": "create",              // 操作标识  
  "entityCode": "user",                // 实体标识
  "data": {                            // 业务数据
    "username": "test",
    "email": "test@example.com"
  },
  "metadata": {                        // 元数据
    "formId": "user_form_001",
    "version": "1.0"
  }
}
```

#### 1.2 权限映射配置
```json
{
  "permission_mappings": {
    "user_management": {
      "create": "user:api:create",
      "update": "user:api:update", 
      "delete": "user:api:delete",
      "query": "user:api:query",
      "export": "user:api:export"
    },
    "order_management": {
      "create": "order:api:create",
      "audit": "order:api:audit",
      "cancel": "order:api:cancel"
    }
  }
}
```

#### 1.3 权限拦截器实现逻辑
```java
@Component
public class LowCodePermissionInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // 1. 判断是否为低代码统一接口
        if (!isLowCodeApi(request)) {
            return true; // 非低代码接口，继续执行
        }
        
        // 2. 解析请求参数
        LowCodeRequest lowCodeRequest = parseRequest(request);
        
        // 3. 构建权限标识
        String permission = buildPermission(
            lowCodeRequest.getModuleCode(), 
            lowCodeRequest.getActionCode()
        );
        
        // 4. 权限验证
        if (!hasPermission(getCurrentUser(), permission)) {
            throw new BusinessException("无权限执行此操作");
        }
        
        return true;
    }
    
    private String buildPermission(String moduleCode, String actionCode) {
        // 从配置中获取权限映射
        return permissionMappingService.getPermission(moduleCode, actionCode);
    }
}
```

### 方案二：动态权限注册

#### 2.1 权限自动注册机制
当低代码平台创建新的表单或配置新的操作时，自动注册对应的权限：

```java
@Service
public class DynamicPermissionService {
    
    /**
     * 注册低代码权限
     */
    public void registerLowCodePermission(LowCodeConfig config) {
        String permissionCode = String.format("%s:api:%s", 
            config.getModuleCode(), 
            config.getActionCode()
        );
        
        // 检查权限是否已存在
        if (!permissionExists(permissionCode)) {
            // 创建权限记录
            Permission permission = new Permission();
            permission.setPermissionCode(permissionCode);
            permission.setPermissionName(config.getActionName());
            permission.setResourceType("API");
            permission.setIsDynamic(true);
            
            permissionRepository.save(permission);
        }
    }
}
```

#### 2.2 权限配置界面
提供可视化界面让管理员配置低代码操作的权限：

```json
{
  "formId": "user_form_001",
  "formName": "用户管理表单",
  "permissions": [
    {
      "actionCode": "create",
      "actionName": "新增用户",
      "permissionCode": "user:api:create",
      "roles": ["admin", "user_manager"]
    },
    {
      "actionCode": "update", 
      "actionName": "编辑用户",
      "permissionCode": "user:api:update",
      "roles": ["admin", "user_manager"]
    }
  ]
}
```

### 方案三：基于URL路径的权限控制

#### 3.1 URL模式匹配
```java
@Component
public class PathBasedPermissionInterceptor implements HandlerInterceptor {
    
    // 配置URL模式与权限的映射关系
    private final Map<String, String> pathPermissionMap = Map.of(
        "/api/lowcode/user/**", "user:api:*",
        "/api/lowcode/order/**", "order:api:*",
        "/api/lowcode/*/create", "*:api:create",
        "/api/lowcode/*/update", "*:api:update"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // 根据路径和方法匹配权限
        String requiredPermission = matchPermission(requestPath, method);
        
        if (requiredPermission != null) {
            return checkPermission(getCurrentUser(), requiredPermission);
        }
        
        return true;
    }
}
```

## 推荐实现方案

### 核心思路：请求参数解析 + 权限映射

#### 1. 统一接口设计
```http
POST /api/lowcode/execute
{
  "action": {
    "module": "user_management",
    "operation": "create",
    "entity": "user"
  },
  "data": {...},
  "context": {
    "formId": "user_form_001",
    "source": "lowcode_platform"
  }
}
```

#### 2. 权限拦截器
```java
@Component
@Order(1)
public class LowCodePermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private PermissionMappingService permissionMappingService;
    
    @Autowired
    private UserPermissionService userPermissionService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // 只拦截低代码接口
        if (!"/api/lowcode/execute".equals(request.getRequestURI())) {
            return true;
        }
        
        try {
            // 解析请求体
            String requestBody = getRequestBody(request);
            LowCodeRequest lowCodeRequest = JSON.parseObject(requestBody, LowCodeRequest.class);
            
            // 构建权限标识
            String permission = buildPermissionCode(lowCodeRequest);
            
            // 获取当前用户
            Long userId = getCurrentUserId(request);
            
            // 权限验证
            if (!userPermissionService.hasPermission(userId, permission)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write(JSON.toJSONString(
                    Result.error(ResponseCode.FORBIDDEN, "无权限执行此操作")
                ));
                return false;
            }
            
            // 将权限信息存储到请求属性中，供后续使用
            request.setAttribute("CURRENT_PERMISSION", permission);
            request.setAttribute("LOWCODE_REQUEST", lowCodeRequest);
            
            return true;
            
        } catch (Exception e) {
            log.error("权限验证失败", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
    }
    
    private String buildPermissionCode(LowCodeRequest request) {
        String module = request.getAction().getModule();
        String operation = request.getAction().getOperation();
        
        // 从配置中获取权限映射，如果没有配置则使用默认规则
        return permissionMappingService.getPermissionCode(module, operation)
            .orElse(String.format("%s:api:%s", module, operation));
    }
}
```

#### 3. 权限映射服务
```java
@Service
public class PermissionMappingService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String PERMISSION_MAPPING_KEY = "permission:mapping";
    
    /**
     * 获取权限映射
     */
    public Optional<String> getPermissionCode(String module, String operation) {
        String key = String.format("%s:%s", module, operation);
        String permission = redisTemplate.opsForHash().get(PERMISSION_MAPPING_KEY, key);
        return Optional.ofNullable(permission);
    }
    
    /**
     * 设置权限映射
     */
    public void setPermissionMapping(String module, String operation, String permission) {
        String key = String.format("%s:%s", module, operation);
        redisTemplate.opsForHash().put(PERMISSION_MAPPING_KEY, key, permission);
    }
    
    /**
     * 批量设置权限映射
     */
    public void batchSetPermissionMapping(Map<String, String> mappings) {
        redisTemplate.opsForHash().putAll(PERMISSION_MAPPING_KEY, mappings);
    }
}
```

#### 4. 低代码控制器
```java
@RestController
@RequestMapping("/api/lowcode")
public class LowCodeController {
    
    @Autowired
    private LowCodeExecuteService lowCodeExecuteService;
    
    @PostMapping("/execute")
    public Result<Object> execute(HttpServletRequest request, 
                                @RequestBody LowCodeRequest lowCodeRequest) {
        
        // 权限信息已经在拦截器中验证并存储
        String permission = (String) request.getAttribute("CURRENT_PERMISSION");
        
        // 执行具体的业务逻辑
        Object result = lowCodeExecuteService.execute(lowCodeRequest, permission);
        
        return Result.success(result);
    }
}
```

## 数据权限处理

对于低代码场景的数据权限，可以在业务执行时进行过滤：

```java
@Service
public class LowCodeExecuteService {
    
    public Object execute(LowCodeRequest request, String permission) {
        
        // 1. 根据操作类型执行相应逻辑
        switch (request.getAction().getOperation()) {
            case "query":
                return executeQuery(request, permission);
            case "create":
                return executeCreate(request, permission);
            case "update":
                return executeUpdate(request, permission);
            case "delete":
                return executeDelete(request, permission);
            default:
                throw new BusinessException("不支持的操作类型");
        }
    }
    
    private Object executeQuery(LowCodeRequest request, String permission) {
        // 构建查询条件
        QueryWrapper<?> queryWrapper = buildQueryWrapper(request);
        
        // 应用数据权限过滤
        applyDataPermission(queryWrapper, permission);
        
        // 执行查询
        return dynamicEntityService.query(request.getAction().getEntity(), queryWrapper);
    }
    
    private void applyDataPermission(QueryWrapper<?> queryWrapper, String permission) {
        // 根据权限添加数据过滤条件
        User currentUser = getCurrentUser();
        
        if (permission.contains(":data:dept")) {
            // 部门数据权限
            queryWrapper.eq("dept_id", currentUser.getDeptId());
        } else if (permission.contains(":data:own")) {
            // 个人数据权限
            queryWrapper.eq("created_by", currentUser.getId());
        }
        // 可以根据需要添加更多数据权限规则
    }
}
```

## 权限配置管理

提供管理界面让管理员配置低代码操作的权限映射：

```java
@RestController
@RequestMapping("/api/admin/permission")
public class PermissionConfigController {
    
    @PostMapping("/lowcode/mapping")
    public Result<Void> setLowCodePermissionMapping(@RequestBody PermissionMappingRequest request) {
        permissionMappingService.setPermissionMapping(
            request.getModule(),
            request.getOperation(), 
            request.getPermission()
        );
        return Result.success();
    }
    
    @GetMapping("/lowcode/mapping")
    public Result<Map<String, String>> getLowCodePermissionMappings() {
        Map<String, String> mappings = permissionMappingService.getAllMappings();
        return Result.success(mappings);
    }
}
```

## 总结

这套方案的核心优势：

1. **灵活性**：支持动态配置权限映射
2. **统一性**：所有低代码操作都通过统一的权限验证流程
3. **可扩展性**：可以轻松添加新的权限规则和数据权限
4. **性能**：使用缓存提高权限检查效率
5. **可维护性**：权限配置与业务代码分离

通过这种方式，你可以对所有低代码操作进行精细的权限控制，无论是菜单权限、操作权限还是数据权限。