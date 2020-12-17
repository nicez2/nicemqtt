package com.nice.mqtt.thread.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentThreadPool implements IConcurrentThreadPool {
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public void initConcurrentThreadPool(int corePoolSize,
                                         int maximumPoolSize,
                                         long keepAliveTime,
                                         TimeUnit unit,
                                         LinkedBlockingDeque<Runnable> deque) {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
               deque);
    }

    @Override
    public <V> V submit(CallableTemplate<V> task) throws InterruptedException,
            ExecutionException {
        Future<V> result = threadPoolExecutor.submit(task);

        return result.get();
    }

    @Override
    public <V> List<V> invokeAll(List<? extends CallableTemplate<V>> tasks)
            throws InterruptedException, ExecutionException {

        List<Future<V>> tasksResult = threadPoolExecutor.invokeAll(tasks);
        List<V> resultList = new ArrayList<V>();

        for (Future<V> future : tasksResult) {
            resultList.add(future.get());
        }

        return resultList;
    }

}
