package com.server.manage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 用于标记需要特定权限才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {
    /**
     * 需要的权限值
     * @return 权限标识数组
     */
    String[] value() default {};

    /**
     * 验证模式
     * AND: 需要满足所有权限
     * OR: 满足任一权限即可
     * @return 验证模式
     */
    Logical logical() default Logical.AND;

    public enum Logical {
        AND, OR
    }
}