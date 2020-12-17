package com.nice.mqtt.thread.pool;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @Author hzdz163@163.com
 * @Description CallableTaskFrameWork
 * @Date 17:03 2020/9/24
 * @Param
 * @return
 **/
public class CallableTaskFrameWork implements ICallableTaskFrameWork {

    private IConcurrentThreadPool concurrentThreadPool = new ConcurrentThreadPool();

    @Override
    public <V> List<V> submitsAll(List<? extends CallableTemplate<V>> tasks, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  LinkedBlockingDeque<Runnable> deque)
            throws InterruptedException, ExecutionException {

        concurrentThreadPool.initConcurrentThreadPool(corePoolSize,
                maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                deque);

        return concurrentThreadPool.invokeAll(tasks);
    }

}
