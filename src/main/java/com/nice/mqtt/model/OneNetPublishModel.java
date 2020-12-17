package com.nice.mqtt.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.fusesource.mqtt.client.BlockingConnection;

/**
 * @Author hzdz163@163.com
 * @Description mqtt 实体类
 * @Date 17:22 2020/9/30 0030
 * @Param
 * @return
 **/
@Data
@ToString
@Accessors(chain = true)
public class OneNetPublishModel {
    private String productId;
    private String deviceName;
    private BlockingConnection connection;
    private String data;
    private Integer count;
    private long publishTime;
}
