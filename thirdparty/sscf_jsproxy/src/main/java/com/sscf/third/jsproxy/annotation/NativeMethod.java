package com.sscf.third.jsproxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  定义JS调用本地的方法
 * {@link #type()} 定义参数传递类型
 * {@link #clazz()} 定义参数解析类型,可以为多个参数，如果为多个参数，则按类型以此解析，默认为空参数
 * {@link #method()} 定义方法代表的常量
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeMethod {

    String JSON = "json";
    String JSONARRAY = "jsonArray";
    String STRING = "string";

    // 参数传递解析类型
    String type() default JSON;

    // 参数类型
    Class[] clazz() default {};

    // 参数名称
    String[] params() default {};

    // 参数对应的唯一Code码
    int method();
}
