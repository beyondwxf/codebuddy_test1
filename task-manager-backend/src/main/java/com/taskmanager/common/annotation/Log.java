package com.taskmanager.common.annotation;

import com.taskmanager.common.enums.BusinessTypeEnum;

import java.lang.annotation.*;

/**
 * 自定义操作日志注解
 * 用于标记需要记录操作日志的方法，配合 LogAspect 切面使用
 *
 * @author taskmanager
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称（如：用户管理、角色管理）
     */
    String title() default "";

    /**
     * 业务操作类型
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * 是否保存请求参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应参数
     */
    boolean isSaveResponseData() default true;
}
