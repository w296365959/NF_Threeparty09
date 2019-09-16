package com.lx.adapterdelegates.binder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * View绑定器，初始化给定id的view
 */
public class ViewBinder {

    /**
     * 若容器是activity可以直接调用此方法
     *
     * @param activity
     */
    public static void inject(Activity activity) {
        inject(activity, activity);
    }

    /**
     * 注入，调用此方法会将fieldContainer中所有带{@link ViewBind}注解的field进行实例化
     *
     * @param fieldContainer 变量的所在容器
     * @param viewContainer  view的所在容器
     */
    public static void inject(Object fieldContainer, Object viewContainer) {
        List<Field> declaredFields = getAllFields(fieldContainer.getClass(), Object.class);
        for (Field field : declaredFields) {
            ViewBind viewBindAnnotation = field.getAnnotation(ViewBind.class);
            if (viewBindAnnotation != null) {
                int idValue = viewBindAnnotation.value();
                int textId = viewBindAnnotation.textId();
                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(fieldContainer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    value = null;
                }

                // 已经付过值不再赋值
                if (value != null) {
                    continue;
                }

                Object injectedValue = null;
                if (viewContainer instanceof View) {
                    injectedValue = fieldType.cast(((View) viewContainer).findViewById(idValue));
                } else if (viewContainer instanceof Activity) {
                    injectedValue = fieldType.cast(((Activity) viewContainer).findViewById(idValue));
                }
                try {
                    if (injectedValue == null) {
                        throw new IllegalStateException("findViewById(" + idValue
                                + ") gave null for " +
                                field + ", can't inject");
                    }
                    // 给textView赋值
                    if (injectedValue instanceof TextView && textId > 0) {
                        ((TextView) injectedValue).setText(textId);
                    }
                    field.set(fieldContainer, injectedValue);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.w("ViewBinder", "view inject " + field.getName() + "error" + e.getMessage());
                }
                field.setAccessible(false);
            }
        }
    }

    /**
     * 获取所有的变量，包括父类的
     *
     * @param clazz    获取变量的类
     * @param endClazz 获取变量的终点
     * @return
     */
    private static List<Field> getAllFields(Class clazz, @NonNull Class endClazz) {
        if (clazz == null || clazz.getName().equals(endClazz.getName())) {
            return new ArrayList<>();
        } else {
            List<Field> list = new ArrayList<>();
            Collections.addAll(list, clazz.getDeclaredFields());
            list.addAll(getAllFields(clazz.getSuperclass(), endClazz));
            return list;
        }
    }
}