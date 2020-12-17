package com.nice.mqtt.thread.pool;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @Author hzdz163@163.com
 * @Description ICallableTaskFrameWork
 * @Date 17:03 2020/9/24
 * @Param
 * @return
 **/
public interface ICallableTaskFrameWork {
    <V> List<V> submitsAll(List<? extends CallableTemplate<V>> tasks, int corePoolSize,
                           int maximumPoolSize,
                           long keepAliveTime,
                           TimeUnit unit,
                           LinkedBlockingDeque<Runnable> deque)
            throws InterruptedException, ExecutionException;
}
