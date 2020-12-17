package com.nice.mqtt.thread.pool;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @Author hzdz163@163.com
 * @Description IConcurrentThreadPool
 * @Date 13:46 2020/9/24
 * @Param
 * @return
 **/
public interface IConcurrentThreadPool {
    /**
     * 初始化线程池
     */
    void initConcurrentThreadPool(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  LinkedBlockingDeque<Runnable> deque);

    /**
     * 提交单个任务
     *
     * @param task
     * @return
     */
    <V> V submit(CallableTemplate<V> task) throws InterruptedException,
            ExecutionException;

    /**
     * 提交多个任务
     *
     * @param tasks
     * @return
     */
    <V> List<V> invokeAll(List<? extends CallableTemplate<V>> tasks)
            throws InterruptedException, ExecutionException;
}
