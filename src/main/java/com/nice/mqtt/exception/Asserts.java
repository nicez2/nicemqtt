package com.nice.mqtt.exception;


/**
 * @Author hzdz163@163.com
 * @Description 断言处理类，用于抛出各种API异常
 * @Date 10:29 2020/9/3
 * @Param
 * @return
 **/
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }
}
