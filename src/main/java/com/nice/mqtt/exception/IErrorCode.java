package com.nice.mqtt.exception;

/**
 * @Author hzdz163@163.com
 * @Description 封装API的错误码
 * @Date 10:26 2020/9/3
 * @Param
 * @return
 **/
public interface IErrorCode {
    /**
     * CODE值
     * @return
     */
    long getCode();

    /**
     * 消息
     * @return
     */
    String getMessage();
}
