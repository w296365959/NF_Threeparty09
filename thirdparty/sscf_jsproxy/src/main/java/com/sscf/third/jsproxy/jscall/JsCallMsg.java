package com.sscf.third.jsproxy.jscall;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

/**
 * JS调用方法描述
 */
public class JsCallMsg {

    public String method;

    public HashMap params;

    public JsCallMsg(String method) {
        this.method = method;
    }

    public boolean isEmptyParams() {
        return  params == null || params.isEmpty();
    }

    public Object key(Object key) {
        return params != null ? params.get(key) : null;
    }

    @NonNull
    @Override
    public String toString() {
        return  "method : " + method + "  : " + JSON.toJSONString(params);
    }
}
