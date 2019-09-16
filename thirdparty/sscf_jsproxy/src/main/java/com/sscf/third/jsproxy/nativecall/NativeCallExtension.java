package com.sscf.third.jsproxy.nativecall;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.sscf.third.jsproxy.annotation.NativeMethod;
import com.sscf.third.jsproxy.impl.INativeMethodCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地调用消息解析
 */
class NativeCallExtension {

    // 存储方法描述
    private static List<MethodCallEntity> cacheClass = new ArrayList<>();

    static void init(Class<INativeMethodCall>... clazz) {
        parseNativeMethod(cacheClass, clazz);
    }

    static @Nullable
    NativeMethod getNativeMethod(int method) {
        for (MethodCallEntity entity : cacheClass) {
            NativeMethod nativeMethod = entity.getNativeMethod(method);
            if (nativeMethod != null) {
                return nativeMethod;
            }
        }
        return null;
    }

    static String getNativeMethodName(int method) {
        for (MethodCallEntity entity : cacheClass) {
            String methodName = entity.getNativeMethodName(method);
            if (methodName != null) {
                return methodName;
            }
        }
        return null;
    }

    static Class<INativeMethodCall> getNativeMethodClass(int method) {
        for (MethodCallEntity entity : cacheClass) {
            if (entity.contains(method)) {
                return entity.clazz;
            }
        }
        return null;
    }

    private static void parseNativeMethod(List<MethodCallEntity> cacheClass, Class<INativeMethodCall>... clazzs) {
        for (Class clazz : clazzs) {
            MethodCallEntity methodCallEntity = new MethodCallEntity(clazz);
            Method[] methods=clazz.getMethods();
            if (methods.length > 0) {
                for (Method method : methods) {
                    // 获取本地调用方法的申明
                    NativeMethod annotation=method.getAnnotation(NativeMethod.class);
                    if (annotation != null) {
                        methodCallEntity.addMethod(annotation.method(), annotation, method.getName());
                    }
                }
            }
            cacheClass.add(methodCallEntity);
        }
    }

    private static class MethodCallEntity {
        private SparseArray<NativeMethod> cacheNativeMethod = new SparseArray();
        private SparseArray<String> cacheMethodName = new SparseArray();
        private Class<INativeMethodCall> clazz;

        protected MethodCallEntity(Class clazz) {
            this.clazz = clazz;
        }

        public void addMethod(int what, NativeMethod method, String methodName) {
            cacheNativeMethod.put(what, method);
            cacheMethodName.put(what, methodName);
        }

        NativeMethod getNativeMethod(int method) {
            return cacheNativeMethod.get(method);
        }

        String getNativeMethodName(int method) {
            return cacheMethodName.get(method);
        }

        boolean contains(int what) {
            return cacheNativeMethod.get(what) != null;
        }
    }
}
