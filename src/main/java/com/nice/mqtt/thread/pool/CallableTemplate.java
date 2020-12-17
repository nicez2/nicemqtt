package com.nice.mqtt.thread.pool;

import java.util.concurrent.Callable;

/**
 * @Author hzdz163@163.com
 * @Description CallableTemplate
 * @Date 17:03 2020/9/24
 * @Param
 * @return
 **/
public abstract class CallableTemplate<V> implements Callable<V> {
    /**
     * 前置处理
     */
    public void beforeProcess() {
    }

    /**
     * 处理业务逻辑
     *
     * @return
     */
    public abstract V process() throws Exception;

    /**
     * 后置处理
     */
    public void afterProcess() {
    }

    @Override
    public V call() throws Exception {
        beforeProcess();
        V result = process();
        afterProcess();
        return result;
    }
}
