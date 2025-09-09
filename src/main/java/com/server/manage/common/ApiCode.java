package com.server.manage.common;

/**
 * API状态码常量定义
 */
public final class ApiCode {

    // 私有构造函数，防止实例化
    private ApiCode() {
        throw new AssertionError("ApiCode is a utility class and cannot be instantiated");
    }

    // 成功状态码
    public static final int SUCCESS = 0;

    // 客户端错误状态码 (4xx)
    public static final int BAD_REQUEST = 400;           // 请求参数错误
    public static final int UNAUTHORIZED = 401;          // 未授权
    public static final int FORBIDDEN = 403;             // 权限不足
    public static final int NOT_FOUND = 404;             // 资源不存在
    public static final int METHOD_NOT_ALLOWED = 405;    // 请求方法不允许
    public static final int CONFLICT = 409;              // 资源冲突
    public static final int VALIDATION_ERROR = 422;      // 参数校验失败

    // 服务器错误状态码 (5xx)
    public static final int INTERNAL_SERVER_ERROR = 500;  // 服务器内部错误
    public static final int BAD_GATEWAY = 502;            // 网关错误
    public static final int SERVICE_UNAVAILABLE = 503;    // 服务不可用
    public static final int GATEWAY_TIMEOUT = 504;        // 网关超时

    // 业务错误状态码 (1000+)
    public static final int USER_NOT_FOUND = 1001;        // 用户不存在
    public static final int USER_ALREADY_EXISTS = 1002;   // 用户已存在
    public static final int INVALID_PASSWORD = 1003;      // 密码错误
    public static final int ACCOUNT_LOCKED = 1004;        // 账户被锁定
    public static final int ACCOUNT_EXPIRED = 1005;       // 账户已过期

    public static final int ROLE_NOT_FOUND = 1101;        // 角色不存在
    public static final int ROLE_ALREADY_EXISTS = 1102;   // 角色已存在
    public static final int ROLE_IN_USE = 1103;           // 角色正在使用中

    public static final int PERMISSION_NOT_FOUND = 1201;  // 权限不存在
    public static final int PERMISSION_DENIED = 1202;     // 权限不足

    // 数据库操作错误状态码 (2000+)
    public static final int DB_CONNECTION_ERROR = 2001;   // 数据库连接错误
    public static final int DB_OPERATION_FAILED = 2002;   // 数据库操作失败
    public static final int DB_CONSTRAINT_VIOLATION = 2003; // 数据库约束违反

    // 第三方服务错误状态码 (3000+)
    public static final int EXTERNAL_SERVICE_ERROR = 3001; // 第三方服务错误
    public static final int API_RATE_LIMIT = 3002;         // API调用限流

    // 通用错误状态码
    public static final int UNKNOWN_ERROR = -1;            // 未知错误
}
