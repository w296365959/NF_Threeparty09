package com.sscf.third.jsproxy.nativecall;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sscf.third.jsproxy.annotation.NativeMethod;
import com.sscf.third.jsproxy.impl.INativeMethodCall;
import com.sscf.third.jsproxy.impl.IWebPage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 处理H5 JS调用本地方法入口
 */
public class NativeCall {

    private IWebPage mPage;

    public NativeCall(IWebPage page) {
        this.mPage = page;
    }

    /**
     * 注册本地实现类, Called on application start
     * @param classes Class<INativeMethodCall>
     */
    @SafeVarargs
    public static void init(Class<INativeMethodCall>... classes) {
        NativeCallExtension.init(classes);
    }

    public boolean call(Message message) {
        try {
            if (isPageValid()) {
                NativeMethod nativeMethod=NativeCallExtension.getNativeMethod(message.what);
                Class<INativeMethodCall> clazz=NativeCallExtension.getNativeMethodClass(message.what);
                if (clazz == null || nativeMethod == null)
                    return false;

                Object instance = clazz.newInstance();
                preparCall(instance);
                // 有参数需要解析
                Class[] clazzs = nativeMethod.clazz();
                if (clazzs.length > 0) {
                    callMethod(instance, message.what, parseParams(message.obj.toString(), nativeMethod));
                }
                // 无参数方法调用
                else {
                    callMethod(clazz.newInstance(), message.what);
                }
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean call(int what, Object... arguments) {
        try {
            if (isPageValid()) {
                Class<INativeMethodCall> clazz=NativeCallExtension.getNativeMethodClass(what);
                if (clazz != null) {
                    Object instance=clazz.newInstance();
                    preparCall(instance);
                    callMethod(instance, what, arguments);
                    return true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 调用之前先 {@link INativeMethodCall#setWebpage(IWebPage)}
     * @param  object Object
     * @return boolean
     */
    private boolean preparCall(Object object) {
        try {
            Method setWebPage=object.getClass().getMethod("setWebpage", IWebPage.class);
            setWebPage.invoke(object, mPage);
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查当前上下文是否有效
     * @return boolean
     */
    private boolean isPageValid() {
        return mPage != null && mPage.getActivity() != null && mPage.fragmentManager() != null;
    }

    public static boolean sendCall(Handler handle, int reqCode) {
        return sendCall(handle, reqCode, null);
    }

    public static boolean sendCall(Handler handle, int reqCode, String message) {
        if (handle != null) {
            Message msg = Message.obtain();
            msg.what = reqCode;
            if (message != null) {
                msg.obj=message;
            }
            handle.sendMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * 调用需要执行的方法
     * @param object Object
     * @param what int
     * @param arguments Object...
     * @return boolean
     */
    private static boolean callMethod(Object object, int what, Object... arguments) {
        NativeMethod nativeMethod = NativeCallExtension.getNativeMethod(what);
        String methodName = NativeCallExtension.getNativeMethodName(what);
        if (nativeMethod != null && object != null) {
            try {
                Method method = object.getClass().getMethod(methodName, nativeMethod.clazz());
                // 调用指定方法
                method.invoke(object, arguments);
                Log.d("NativeCall", "==========>>> invoke method " + object.getClass() + "#" + methodName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static Object[] parseParams(String message, NativeMethod nativeMethod) throws Exception{
        Class[] clazz=nativeMethod.clazz();
        @Nullable String[] params = nativeMethod.params();
        if (clazz.length == 0 || TextUtils.isEmpty(message)) {
            return null;
        }

        // 普通JSON对象
        if (NativeMethod.JSON.equals(nativeMethod.type())) {
            // 单JSON对象
            if (nativeMethod.clazz().length == 1) {
                Object[] objects = new Object[1];
                objects[0] =JSON.parseObject(message, clazz[0]);
                return objects;
            }
            // 组合JSON对象
            else if (nativeMethod.clazz().length > 1) {
                int min = Math.min(clazz.length, params.length);
                Object[] objects = new Object[min];
                JSONObject jsonObject = JSON.parseObject(message);
                for (int i=0; i<min; i++) {
                    objects[i] = jsonObject.getObject(params[i], clazz[i]);
                }
            }
            return null;
        } else if (NativeMethod.JSONARRAY.equals(nativeMethod.type())){
            JSONArray array = JSON.parseArray(message);
            int arraySize = array.size();
            int minSize = Math.min(arraySize, clazz.length);
            Object[] objects = new Object[minSize];
            for (int i=0; i<minSize; i++) {
                objects[i] = array.getObject(i, clazz[i]);
            }
            return objects;
        } else if (NativeMethod.STRING.equals(nativeMethod.type())){
            String[] strings = message.split(",");
            if (strings.length > 0) {
                return strings;
            } else {
                return new String[] {message};
            }
        }
        return null;
    }
}
