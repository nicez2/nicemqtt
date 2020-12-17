package com.nice.mqtt.params;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @Author hzdz163@163.com
 * @Description mqtt配置参数
 * @Date 13:13 2020/9/30 0030
 * @Param
 * @return
 **/
@Data
@ToString
@Accessors(chain = true)
public class MqttConfigParams {
    private String url;
    private String port;
    private String version;
    private Integer count;
    private Integer wait;
    private Integer sendCount;
    private String sendData;
}
