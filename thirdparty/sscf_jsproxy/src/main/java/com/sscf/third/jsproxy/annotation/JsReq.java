package com.sscf.third.jsproxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link #method()} js回调方法名称
 * {@link #type()} js回调参数类型
 * {@link #require()} js回调需要处理参数
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsReq {

    String JSON = "json";
    String STRING = "string";

    String method();

    String type() default JSON;

    String[] require() default "";
}
