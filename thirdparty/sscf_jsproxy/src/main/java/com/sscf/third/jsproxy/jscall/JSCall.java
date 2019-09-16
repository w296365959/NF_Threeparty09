package com.sscf.third.jsproxy.jscall;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sscf.third.jsproxy.impl.IJSMethodCall;
import com.tencent.smtt.sdk.WebView;

/**
 * 定义本地调用H5的所有方法定义
 *
 * （1） 添加新调用接口 ，增加一个 public static final int 常量值
 * （2） 为此值添加{@link JsReq}注解
 *
 * 直接调用JS
 * {@link #call(DtWebView, Message)} 处理Handle中消息
 * {@link #call(DtWebView, int, Object...)} 直接发起JS调用
 *
 * 发起Handle Message
 * {@link #sendCall(Handler, int)} 无参Message
 * {@link #sendCall(Handler, int, String)} 带参构造函数，参数可以为JSON格式和普通string格式，普通String格式多参数用","分隔
 */
@SuppressWarnings("ALL")
public class JSCall {

    /**
     * 注册本地实现类, Called on application start
     * @param classes Class<IJSMethodCall>
     */
    @SafeVarargs
    public static void init(Class<IJSMethodCall>... classes) {
        JSCallExtension.init(classes);
    }

    public static boolean call(final WebView webView, int req, Object... params) {
        JsCallMsg callMsg = JSCallExtension.parseMessage(req, params);
        if (callMsg != null) {
            JSCallExtension.requireJs(webView, callMsg);
            Log.d("JSCall", "========>>> JsCallMsg " + callMsg.toString());
            return true;
        }
        return false;
    }

    public static boolean call(final WebView webView, Message message) {
        JsCallMsg callMsg = JSCallExtension.parseMessage(message);
        if (callMsg != null) {
            JSCallExtension.requireJs(webView, callMsg);
            Log.d("JSCall", "========>>> JsCallMsg " + callMsg.toString());
            return true;
        }
        return false;
    }

    public static boolean sendCall(Handler handle, int reqCode) {
        return sendCall(handle, reqCode, null);
    }

    public static boolean sendCall(Handler handle, int reqCode, String message) {
        if (handle != null) {
            Message msg=Message.obtain();
            msg.what = reqCode;
            if (message != null) {
                msg.obj=message;
            }
            handle.sendMessage(msg);
            return true;
        }
        return false;
    }
}
