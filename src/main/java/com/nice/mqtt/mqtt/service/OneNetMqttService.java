package com.nice.mqtt.mqtt.service;


import com.nice.mqtt.params.MqttConfigParams;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author hzdz163@163.com
 * @Description mqtt测试
 * @Date 17:22 2020/9/30 0030
 * @Param
 * @return
 **/
public interface OneNetMqttService {
    /**
     * mqtt 测试
     *
     * @param params
     * @param file
     * @return
     * @throws Exception
     */
    Map<String, Object> createConnection(MqttConfigParams params, MultipartFile file) throws Exception;
}
