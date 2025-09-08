package com.server.manage.exception;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    private int code;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.code = -1;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}