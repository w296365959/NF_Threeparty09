package com.sscf.third.taskpoll.execute.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author leixin
 * 任务池请求参数
 */
@SuppressWarnings("ALL")
public class ProcessParam implements Serializable {

    public HashMap<String, Object> params = new HashMap<>();

    public ProcessParam() {

    }

    public ProcessParam(ProcessParam param, String tag) {
        params.put(tag, param);
    }

    public ProcessParam(String key, Object value) {
        params.put(key, value);
    }

    public ProcessParam put(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public ProcessParam remove(String key) {
        params.remove(key);
        return this;
    }

    public <T> T get(String key) {
        return get(key, null);
    }

    /**
     * 获取key对应的值
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> T get(String key, T value) {
        try {
            return (T)params.get(key);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return value;
    }

    public boolean hasKey(String key) {
        return params.containsKey(key);
    }

    public boolean hasValue(Object value) {
        Iterator it = params.values().iterator();
        while (it.hasNext()) {
            if (it.next().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取value对应的key
     * @param value
     * @return
     */
    public String getValueKey(Object value) {
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (params.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }
}
