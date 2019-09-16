package com.sscf.third.jsproxy.jscall;

import android.os.Message;
import android.text.TextUtils;

import com.sscf.third.jsproxy.annotation.JsReq;
import com.sscf.third.jsproxy.impl.IJSMethodCall;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Js回调处理类，提供更灵活的JS处理方式
 * 新增JS回调方式如下
 * （1）在{@link JSCall} 新增一个static final int 的静态变量
 * （2）对这个静态变量添加{@link JsReq}的注解
 */
public class JSCallExtension {

    private static HashMap<Object, JsReq> cacheMap = new HashMap<>();

    static void init(Class<IJSMethodCall>... clazzs) {
        parseclass(cacheMap, clazzs);
    }

    /**
     * 处理请求参数
     * @param request Object
     * @param params Object...
     * @return JsCallMsg
     */
    static JsCallMsg parseMessage(Object request, Object... params) {
        JsCallMsg jsCallMsg = null;
        JsReq req = cacheMap.get(request);
        if (req != null) {
            String method=req.method();
            String[] paramsKey = req.require();
            if (!TextUtils.isEmpty(method)) {
                jsCallMsg = new JsCallMsg(method);
                jsCallMsg.params = parseParams(paramsKey, params);
            }
        }
        return jsCallMsg;
    }

    /**
     * 处理Handle的Message,将message中的信息解析为一个{@link JsCallMsg}
     * @param  message Message
     * @return boolean handle or not
     */
    static JsCallMsg parseMessage(Message message) {
        int messageWhat = message.what;
        try {
            JsCallMsg jsCallMsg;
            JsReq req=cacheMap.get(messageWhat);
            if (req != null) {
                String method=req.method();
                String[] paramsKey = req.require();
                if (!TextUtils.isEmpty(method)) {
                    jsCallMsg = new JsCallMsg(method);
                    // 是否有参数要处理
                    if (message.obj != null && paramsKey.length > 0) {
                        // 参数类型是否为json
                        if (JsReq.JSON.equals(req.type())) {
                            try {
                                JSONObject object=new JSONObject((String) message.obj);
                                // 获取require参数
                                HashMap map=new HashMap();
                                for (String string : paramsKey) {
                                    map.put(string, object.get(string));
                                }
                                jsCallMsg.params = map;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // 参数为普通数据类型
                        else if (JsReq.STRING.equals(req.type())){
                            HashMap map=new HashMap();
                            // 多个参数处理
                            if (paramsKey.length > 1) {
                                String string = String.valueOf(message.obj);
                                String[] params = string.split(",");
                                for (int i=0; i< Math.min(params.length, paramsKey.length); i++) {
                                    map.put(paramsKey[i], params[i]);
                                }
                            } else {
                                map.put(paramsKey[0], message.obj);
                            }
                            jsCallMsg.params = map;
                        }
                    }
                    return jsCallMsg;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 通过参数Key和参数，生成对应的CallMsg
     * @param paramsKey String[]
     * @param params Object...
     * @return HashMap
     */
    private static final HashMap parseParams(String[] paramsKey, Object... params) {
        if (paramsKey != null && paramsKey.length > 0) {
            HashMap<String, Object> hashMap = new HashMap<>();
            for (int i=0; i< Math.min(params.length, paramsKey.length); i++) {
                hashMap.put(paramsKey[0], params[i]);
            }
            return hashMap;
        }
        return null;
    }

    /**
     * Js回调调用处理
     * @param jsCallMsg JsCallMsg
     */
    public static void requireJs(WebView webView, JsCallMsg jsCallMsg) {
        if (webView == null || jsCallMsg == null) {
            return;
        }

        // json样式参数
        final JSONObject params = new JSONObject();
        if (!jsCallMsg.isEmptyParams()) {
            for (Object key : jsCallMsg.params.keySet()) {
                try {
                    params.put(key.toString(), jsCallMsg.key(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            String string = params.toString();
            webView.loadUrl(String.format("javascript:ReqWeb('%s',%s)", jsCallMsg.method, string));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 解析当前类中待处理的Js调用
     * @param hashMap HashMap<Object, JsReq>
     * @param clazzs Class...
     */
    private static void parseclass(HashMap<Object, JsReq> hashMap, Class<IJSMethodCall>... clazzs) {
        try {
            for (Class cls : clazzs) {
                Field[] declaredFields=cls.getDeclaredFields();
                for (Field field : declaredFields) {
                    JsReq jsReq=field.getAnnotation(JsReq.class);
                    if (jsReq != null) {
                        boolean isStatic=Modifier.isStatic(field.getModifiers());
                        if (isStatic) {
                            Object key=field.getInt(null);
                            hashMap.put(key, jsReq);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
