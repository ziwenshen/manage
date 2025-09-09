package com.server.manage.common;

import java.io.Serializable;

/**
 * 统一接口返回体
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private int code;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, ApiCode.SUCCESS, "success", data);
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, ApiCode.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(ApiCode.UNKNOWN_ERROR, message);
    }

    // 快捷方法，用于常见的错误响应
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(ApiCode.BAD_REQUEST, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(ApiCode.UNAUTHORIZED, message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return error(ApiCode.FORBIDDEN, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(ApiCode.NOT_FOUND, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return error(ApiCode.CONFLICT, message);
    }

    public static <T> ApiResponse<T> validationError(String message) {
        return error(ApiCode.VALIDATION_ERROR, message);
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return error(ApiCode.INTERNAL_SERVER_ERROR, message);
    }

    // 业务错误快捷方法
    public static <T> ApiResponse<T> userNotFound() {
        return error(ApiCode.USER_NOT_FOUND, "用户不存在");
    }

    public static <T> ApiResponse<T> userAlreadyExists() {
        return error(ApiCode.USER_ALREADY_EXISTS, "用户已存在");
    }

    public static <T> ApiResponse<T> invalidPassword() {
        return error(ApiCode.INVALID_PASSWORD, "密码错误");
    }

    public static <T> ApiResponse<T> permissionDenied() {
        return error(ApiCode.PERMISSION_DENIED, "权限不足");
    }

    // getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
