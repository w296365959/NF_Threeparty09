package com.sscf.third.taskpoll.execute.process;

import android.support.annotation.NonNull;

import com.sscf.third.taskpoll.execute.TaskBuilder;
import com.sscf.third.taskpoll.execute.ThreadUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author leixin
 * 一个可执行任务组，具有以下特性
 *  {@link #doInBackground(ProcessParam)} {@link #onPostExecute(ProcessParam, Object)} 都会将失效
 * （1）{@link #POST_TOGETHER}：随机执行，一起返回
 * （2）{@link #POST_SEQUENTIALLY} 随机执行，顺序返回
 */
@SuppressWarnings("ALL")
public class ProcessableGroup<T> extends Processable {

    private static final int POST_TOGETHER = -2;

    private static final int POST_SEQUENTIALLY = -3;

    private int postMode = POST_TOGETHER;

    /**
     * 存储一组任务，所有任务结束后清空列表
     */
    private LinkedHashMap<Long, Processable> processableArray = new LinkedHashMap<>();

    /**
     * 缓存任务执行结果
     */
    private LinkedHashMap<Long, T> resultArray = new LinkedHashMap<>();

    /**
     * 等待被Post的任务序列
     */
    private LinkedList<Long> waittingPostQueue = new LinkedList<>();

    private byte[] _lock = new byte[0];

    /**
     * 最终返回结果，次数返回结果顺序应对post执行时参数顺序，在所有任务执行完成后，此方法一定会被调用
     * @param results 执行结果，results中将会返回所有的Process的执行结果
     */
    public void postResult(T... results) {
        System.out.println("=================>>>> postResult results" + results.length);

    }

    /**
     * 随机执行，同时返回
     * @param processables 需要同步执行的Processable
     */
    public void postTogether(Processable<T>... processables) {
        for (Processable<T> processable : processables) {
            long thread_id = processable.getThreadID();
            processableArray.put(thread_id, processable);
            waittingPostQueue.add(thread_id);
        }
        postMode = POST_TOGETHER;
    }

    /**
     * 随机执行，顺序返回，（如果有后续的任务先执行完，会缓存当前任务，结果，并在前面的任务均完成后，调用post返回结果）
     * @param processables 需要序列执行的Processable
     */
    public void postSequentially(Processable<T>... processables) {
        for (Processable<T> processable : processables) {
            long thread_id = processable.getThreadID();
            processableArray.put(thread_id, processable);
            waittingPostQueue.add(thread_id);
        }
        postMode = POST_SEQUENTIALLY;
    }

    @Override
    @Deprecated
    protected Object doInBackground(ProcessParam params) throws Exception {
        return null;
    }

    @Override
    @Deprecated
    protected void onPostExecute(ProcessParam params, Object result) {

    }

    @Override
    public void run() {
        startProcessable();
        for (Processable<T> processable : processableArray.values()) {
            GroupEntity groupEntity = new GroupEntity<T>(processable) {
                @Override
                protected synchronized void onPostExecute(ProcessParam bundle, @NonNull T result) {
                    long thread_id = processable.getThreadID();
                    resultArray.put(thread_id, result);

                    // 如果为顺序执行，则判断是否可以post结果
                    if (postMode == POST_SEQUENTIALLY) {
                        // 如果为返回头元素，则直接post
                        int currentposition = waittingPostQueue.indexOf(thread_id);
                        if (currentposition != -1) {
                            checkHeaderCanPost(waittingPostQueue.peekFirst());
                        }
                    }
                    // POST_TOGETHER模式直接等待最后返回任务
                    else {
                        waittingPostQueue.remove(thread_id);
                    }

                    // 检查是否所有的任务都已执行完成
                    synchronized (_lock) {
                        boolean hasAllExecute = hasAllExecute();
                        if (hasAllExecute && resultArray.size() > 0) {
                            ArrayList<T> arrayList = new ArrayList<>();
                            for (Processable<T> processable : processableArray.values()) {
                                arrayList.add(resultArray.get(processable.getThreadID()));
                            }
                            if (executeMainThread) {
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        postResult((T[])arrayList.toArray());
                                    }
                                });
                            } else {
                                postResult((T[])arrayList.toArray());
                            }

                            // 提示这组任务结束
                            ProcessableGroup.this.endProcessable(resultArray);
                            resultArray.clear();
                        }
                    }
                }
            };
            TaskBuilder.build().execute(groupEntity);
        }
    }

    /**
     * 轮询检测消息头是否可以被返回
     * @param header_task_id
     * @return
     */
    private void checkHeaderCanPost(long header_task_id) {
        if (resultArray.containsKey(header_task_id)) {
            Processable processable = processableArray.get(header_task_id);
            processable.onPostExecute(processable.params, resultArray.get(header_task_id));
            waittingPostQueue.poll();
            // 继续检测header
            Long task_id = waittingPostQueue.peekFirst();
            if (task_id != null) {
                checkHeaderCanPost(task_id);
            }
        }
    }

    private boolean hasAllExecute() {
        for (Processable<T> processable :processableArray.values()) {
            if (!resultArray.containsKey(processable.getThreadID())) {
                return false;
            }
        }
        return true;
    }

    private boolean hasExecute(long... thread_ids) {
        for (long thread_id : thread_ids){
            if (!resultArray.containsKey(thread_id))
                return false;
        }
        return true;
    }

    /**
     * 封装给定的任务，代理执行{@link #doInBackground(ProcessParam)}, 并将结果暂存
     */
    static abstract class GroupEntity<T> extends Processable<T> {

        Processable<T> processable;

        GroupEntity(@NonNull Processable<T> processable) {
            this.processable = processable;
            params(processable.params);
        }

        @Override
        protected T doInBackground(ProcessParam params) throws Exception {
            return processable.doInBackground(params);
        }
    }
}
