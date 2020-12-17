package com.nice.mqtt.controller;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nice.mqtt.api.CommonResult;
import com.nice.mqtt.mqtt.service.OneNetMqttService;
import com.nice.mqtt.params.MqttConfigParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * @Author hzdz163@163.com
 * @Description mqtt测试
 * @Date 13:14 2020/9/23
 * @Param
 * @return
 **/
@Controller
@Api(tags = "设备接入mqtt测试模块")
@ApiOperation(value = "OneNet mqtt Controller")
@ApiSupport(author = "hzdz163@163.com")
@RequestMapping("/mqtt")
public class OneNetMqttController {

    @Autowired
    public OneNetMqttService mqttService;


    @ApiOperation("接入mqtt并上传数据点")
    @RequestMapping(value = "/connection", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult connection(@RequestParam String brokerUrl,
                                   @RequestParam String brokerPort,
                                   @RequestParam String brokerVersion,
                                   @RequestParam Integer threadCount,
                                   @RequestParam Integer waitTime,
                                   @RequestParam Integer sendCount,
                                   @RequestParam String sendData,
                                   @RequestBody MultipartFile file) throws Exception {
        MqttConfigParams params = new MqttConfigParams();
        params.setUrl(brokerUrl).setPort(brokerPort)
                .setVersion(brokerVersion)
                .setCount(threadCount)
                .setWait(waitTime)
                .setSendCount(sendCount)
                .setSendData(sendData);
        Map<String, Object> map;
        map = mqttService.createConnection(params, file);
        return CommonResult.success(map);
    }


}
