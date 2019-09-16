package com.sscf.third.taskpoll.execute.process;

import android.support.annotation.NonNull;

import com.sscf.third.taskpoll.execute.ThreadUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author leixin
 */
@SuppressWarnings("ALL")
public abstract class Processable<T> implements Runnable, Cloneable {

    // 任务延时启动
    private long delay;

    // 全局Thread_id计数器
    private static int CURRENT_THREAD_ID = 0;

    // 唯一标识符
    private long thread_id;

    private long startWaitTime;

    private byte[] _lock = new byte[0];

    boolean executeMainThread = true;

    // 需要处理的参数，又逻辑层自己带入到Processable中
    ProcessParam params;

    private T result = null;
    /**
     * 执行后台操作
     * @return 返回任务执行结果
     */
    protected abstract T doInBackground(ProcessParam params) throws Exception;

    /**
     * 抛出需要处理的结果
     * @param  params 输入参数处理
     * @param result 执行结果
     */
    protected abstract void onPostExecute(ProcessParam params, @NonNull T result);

    /**
     * 设置线程配置参数
     * @param params
     * @return
     */
    public Processable params(ProcessParam params) {
        this.params = params;
        return this;
    }

    /**
     * 是否在MainThread执行
     * @param inMainThread
     * @return
     */
    public Processable executeMainThread(boolean inMainThread) {
        this.executeMainThread = executeMainThread;
        return this;
    }

    // 监听任务状态变化
    CopyOnWriteArrayList<ProcessableListener> mListeners = new CopyOnWriteArrayList<>();

    /**
     * 获取Process的Listener
     * @return
     */
    public List<ProcessableListener> getListeners() {
        return mListeners;
    }

    public void addListener(ProcessableListener listener) {
        if (mListeners == null) return;
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(ProcessableListener listener) {
        mListeners.remove(listener);
    }

    public Processable() {
        synchronized (_lock) {
            thread_id = CURRENT_THREAD_ID++;
        }
    }

    /**
     * 开始执行Processable
     */
    protected final void startProcessable() {
        // start a processable
        for (ProcessableListener listener : mListeners) {
            listener.onStart(this, params);
        }
    }

    /**
     * 结束执行Processable
     */
    protected final void endProcessable(Object result) {
        for (ProcessableListener listener : mListeners) {
            listener.onStop(this, params, result);
        }
    }

    /**
     * 设置取消处理,由子类实现
     */
    public void cancel() {
        Thread.currentThread().interrupt();
        for (ProcessableListener listener : mListeners) {
            listener.onCancel(this, params);
        }
    }

    /**
     * 异常处理
     * @param exception
     */
    protected void onException(Exception exception) {

    }

    public long getThreadID() {
        return thread_id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Processable && thread_id == ((Processable)o).getThreadID();
    }

    public void setDelay(long delay) {
        startWaitTime = System.currentTimeMillis();
        this.delay = delay;
    }

    public boolean isNeedWait(){
        if(System.currentTimeMillis() - startWaitTime < delay){
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        // 异常直接remove掉此次任务
        try {
            startProcessable();

            if (!Thread.currentThread().isInterrupted()) {
                result = doInBackground(params);
            }
            // run on ui thread
            if (!Thread.currentThread().isInterrupted()) {
                if (executeMainThread) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute(params, result);
                        }
                    });
                } else {
                    onPostExecute(params, result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onException(e);
        }
        endProcessable(result);
    }

    @Override
    protected Processable clone() throws CloneNotSupportedException {
        try {
            final Processable processable = (Processable) super.clone();
            if (mListeners != null) {
                CopyOnWriteArrayList<ProcessableListener> oldListeners = mListeners;
                processable.mListeners = new CopyOnWriteArrayList();
                int numListeners = oldListeners.size();
                for (int i = 0; i < numListeners; ++i) {
                    processable.mListeners.add(oldListeners.get(i));
                }
            }
            return processable;
        } catch (Exception exception) {
            throw new AssertionError();
        }
    }

    /**
     * 任务执行状态监听器
     */
    public interface ProcessableListener<T extends Processable> {

        void onStart(T processable, ProcessParam params);

        void onStop(T processable, ProcessParam params, Object result);

        void onCancel(T processable, ProcessParam params);
    }
}
