package com.sscf.third.taskpoll.execute;

/**
 * 创建TaskExecute.
 * 通过轻量级任务池，限制和控制并发任务
 */
public class TaskBuilder {

    private static TaskExecuter standExecute = null;

    /**
     * 获取一个已有的Executer，如果不存在，则创建
     * @return TaskExecuter
     */
    public static TaskExecuter build() {
        if (standExecute == null) {
            standExecute = new TaskExecuter(5, 150);
        }
        return standExecute;
    }

    /**
     * 创建一个新的Executer，由用户自己管理和销毁
     * @param maxexecute 最大并发数
     * @return TaskExecuter
     */
    public static TaskExecuter build(int maxexecute) {
        return new TaskExecuter(maxexecute);
    }
}
