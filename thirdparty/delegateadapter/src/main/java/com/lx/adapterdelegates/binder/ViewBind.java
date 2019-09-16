package com.lx.adapterdelegates.binder;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * View 注入的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewBind {

    /**
     * view对应的viewId
     *
     * @return
     */
    @IdRes int value();

    /**
     * 如果是TextView，会默认将此id对应的字符串赋值给TextView
     *
     * @return
     */
    @StringRes int textId() default -1;
}
