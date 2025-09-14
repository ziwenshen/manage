package com.server.manage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一权限注解
 * 用于标记需要特定权限才能访问的接口
 * 
 * 使用示例：
 * @HasPermission("menu:user:view")
 * @HasPermission(value = {"menu:user:view", "menu:user:edit"}, logical = Logical.OR)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    
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
    
    /**
     * 权限描述，用于文档和错误提示
     * @return 权限描述
     */
    String description() default "";
    
    /**
     * 是否允许匿名访问（当用户未登录时）
     * @return 是否允许匿名访问
     */
    boolean allowAnonymous() default false;
    
    public enum Logical {
        AND, OR
    }
}
