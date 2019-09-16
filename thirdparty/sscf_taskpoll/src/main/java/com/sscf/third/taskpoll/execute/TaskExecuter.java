package com.sscf.third.taskpoll.execute;

import com.sscf.third.taskpoll.execute.process.ProcessParam;
import com.sscf.third.taskpoll.execute.process.Processable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 任务容器管理类.
 */
@SuppressWarnings("ALL")
public class TaskExecuter {

    // 正在执行的任务
    private ConcurrentHashMap<Long, Processable> inProcess = new ConcurrentHashMap<>();

    // 等待队列中的任务
    private LinkedBlockingQueue<Processable> waitingProcess = new LinkedBlockingQueue<>();

    // 最大执行线程数
    private int MAXTHREAD = 5;

    private int TIMEDURING = 250;

    /**
     * 线程启动管理
     */
    private MainLooper looper;

    /**
     * 此锁用于任务主Looper在将task从waitingProcess中取出并放入inProcess中时导致其他线程判断执行任务数量错误的问题
     */
    private final Object lockForTaskChanged = new Object();

    private Processable.ProcessableListener stateChangeListener = new Processable.ProcessableListener() {

        @Override
        public void onStart(Processable processable, ProcessParam params) {

        }

        @Override
        public void onStop(Processable processable, ProcessParam params, Object result) {
            if (inProcess.contains(processable)) {
                // 移除线程状态监听器
                processable.removeListener(this);

                // 移出任务执行队列
                inProcess.remove(processable.getThreadID());
            }
        }

        @Override
        public void onCancel(Processable processable, ProcessParam params) {
            if (inProcess.contains(processable)) {
                inProcess.remove(processable);
            }
            if (waitingProcess.contains(processable)) {
                waitingProcess.remove(processable.getThreadID());
            }
        }
    };

    TaskExecuter(int maxexecute) {
        this(maxexecute, 250);
    }

    TaskExecuter(int maxexecute, int timeduring) {
        MAXTHREAD = maxexecute;
        TIMEDURING = timeduring;
    }

    public void execute(Processable processable) {
        execute(processable, true);
    }

    public void execute(Processable processable, boolean newThread) {
        execute(processable, newThread, 0L);
    }

    /**
     * 启动一个新任务，添加到线程执行队列
     * @param processable  任务runnable
     * @param newThread 是否创建新的执行，如果已经执行
     * @param delay
     */
    public void execute(Processable processable, boolean newThread, long delay){
        try {
            processable.setDelay(delay);
            processable.addListener(stateChangeListener);
            long thread_id = processable.getThreadID();
            if (!newThread) {
                if (inProcess.containsKey(thread_id)) {
                    inProcess.get(thread_id).cancel();
                }
            }
            // 加入线程等待队列
            if (!waitingProcess.contains(processable)) {
                waitingProcess.put(processable);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startLooper();
    }

    /**
     * 检测是否存在任务队列中
     *
     * @param thread_id
     * @return
     */
    public boolean hasProcess(long thread_id) {
        // 如果线程已经开启，则直接返回
        return inProcess.containsKey(thread_id);
    }

    /**
     * 获取一个等待队列的任务任务
     * @param thread_id
     * @return
     */
    public Processable getProcess(long thread_id) {
        for (Processable processable : waitingProcess) {
            if (processable != null && processable.getThreadID() == thread_id) {
                return processable;
            }
        }
        return null;
    }

    /**
     * 销毁一个正在执行的任务
     * @param thread_id
     */
    public boolean cancelRunning(long thread_id) {
        if (inProcess.containsKey(thread_id)) {
            inProcess.get(thread_id).cancel();
            return true;
        }
        return false;
    }

    /**
     * 取消队列中所有的任务
     */
    public void cancelAll() {
        waitingProcess.clear();
        for (Processable runnable : inProcess.values()) {
            runnable.cancel();
        }
    }

    /**
     * 销毁所有任务
     */
    public void destory() {
        if (looper != null &&!looper.isInterrupted()) {
            looper.interrupt();
        }
        looper = null;
        cancelAll();
    }

    /**
     * 启动线程looper
     */
    private synchronized void startLooper() {
        if (looper == null || looper.isInterrupted() || !looper.isAlive()) {
            looper = new MainLooper();
        }

        if (!looper.isAlive()) {
            looper.start();
        }
    }

    /**
     * 正在执行的线程数
     * @return
     */
    protected int runningSize() {
        return inProcess.size();
    }

    /**
     * 等待执行的数量
     * @return
     */
    protected int waitingSize() {
        return waitingProcess.size();
    }

    /**
     * 获取线程总数量
     * @return
     */
    protected int getTaskSize() {
        synchronized (lockForTaskChanged) {
            return inProcess.size() + waitingProcess.size();
        }
    }

    /**
     * 主线程
     */
    private class MainLooper extends Thread {
        @Override
        public void run() {
            // 检测等待队列中的消息是否已经执行完成
            System.out.println("*************************************** mainLooper start *************************");
            while (waitingProcess.size() > 0) {
                // 等待正在执行的队列中的消息执行
                while (inProcess.size() >= MAXTHREAD) {
                    try {
                        sleep(TIMEDURING);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (lockForTaskChanged) {
                    Processable processable = waitingProcess.poll();
                    if(processable != null){
                        if(processable.isNeedWait()){
                            waitingProcess.offer(processable);
                        }else{
                            // 监听任务完成处理
                            processable.addListener(stateChangeListener);
                            // 开始执行异步任务
                            new Thread(processable).start();
                            inProcess.put(processable.getThreadID(), processable);
                        }
                    }
                }

                // 判断当前waiting列表中所有都是delay状态的话，强行sleep一个时间
                if(isAllWaitingProcessDelayed()){
                    try {
                        Thread.sleep(TIMEDURING);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 当所有的任务都轮询完，结束线程
            System.out.println("*************************************** mainLooper interrupt *************************");
//            interrupt();
        }
    }

    /**
     * 判断是否所有的任务都在delay中
     * @return
     */
    private boolean isAllWaitingProcessDelayed(){
        for(Processable processable : waitingProcess){
            if(!processable.isNeedWait()){
                return false;
            }
        }
        return true;
    }
}
