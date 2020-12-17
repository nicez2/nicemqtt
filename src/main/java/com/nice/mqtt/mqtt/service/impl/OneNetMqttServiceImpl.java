package com.nice.mqtt.mqtt.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.nice.mqtt.model.ExcelMode;
import com.nice.mqtt.model.OneNetPublishModel;
import com.nice.mqtt.mqtt.service.OneNetMqttService;
import com.nice.mqtt.params.MqttConfigParams;
import com.nice.mqtt.thread.pool.CallableTaskFrameWork;
import com.nice.mqtt.thread.pool.CallableTemplate;
import com.nice.mqtt.thread.pool.ICallableTaskFrameWork;
import com.nice.mqtt.util.OneNetTokenUtil;
import lombok.SneakyThrows;
import org.apache.poi.util.IOUtils;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author hzdz163@163.com
 * @Description mqtt测试
 * @Date 17:21 2020/9/30 0030
 * @Param
 * @return
 **/
@Service
public class OneNetMqttServiceImpl implements OneNetMqttService {


    private static final Logger logger = LoggerFactory.getLogger(OneNetMqttServiceImpl.class);
    /**
     * mqtt url
     */
    private static String HOST_URL;
    /**
     * mqtt port
     */
    private static int HOST_PORT;
    /**
     * mqtt version
     */
    private static String VERSION;

    @Value("${accesskey}")
    private String accesskey;

    @Value("${productId}")
    private String productId;

    @Override
    public Map<String, Object> createConnection(MqttConfigParams params, MultipartFile file) throws Exception {
        HOST_URL = params.getUrl();
        HOST_PORT = Integer.    parseInt(params.getPort());
        VERSION = params.getVersion();

        Map<String, Object> resultMap = new HashMap<>();
        //核心线程池大小
        int corePoolSize = params.getCount();
        //最大线程池大小
        int maximumPoolSize = params.getCount();
        //线程最大空闲时间
        long keepAliveTime = 0L;
        //时间单位
        TimeUnit unit = TimeUnit.MINUTES;
        ICallableTaskFrameWork callableTaskFrameWork = new CallableTaskFrameWork();
        List<CallableTemplate<OneNetPublishModel>> tasks = new LinkedList<CallableTemplate<OneNetPublishModel>>();
        ConnectionThreadHandler connectionThreadHandler = null;
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        List<String> list = getExcelModeListBytes(bytes);
        logger.info("设备列表，list={}", list);
        for (String deviceName : list) {
            connectionThreadHandler = new ConnectionThreadHandler(deviceName, params.getSendData(), params.getSendCount());
            tasks.add(connectionThreadHandler);
        }

        //结束时间
        Instant start = Instant.now();
        //创建线程池
        List<OneNetPublishModel> results = callableTaskFrameWork
                .submitsAll(tasks, corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingDeque<Runnable>());
        //新开一线程任务自动关闭链接
        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
        scheduled.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for (OneNetPublishModel oneNetPublishModel : results) {
                    oneNetPublishModel.getConnection().disconnect();
                }
                logger.info("自动超时关闭mqtt连接");
                scheduled.shutdown();
            }
        }, 60 + params.getWait(), 20, TimeUnit.SECONDS);

        //结束时间
        Instant finish = Instant.now();
        //计算耗时
        long timeElapsed = Duration.between(start, finish).toMillis();
        //设备列表
        resultMap.put("deviceList", list);
        //请求参数
        resultMap.put("info", params);
        //连接总耗时
        resultMap.put("connectionTime", timeElapsed);
        //连接mqtt实体列表
        results.removeAll(Collections.singleton(null));
        resultMap.put("oneNetPublishModel", JSONUtil.parseArray(results));

        ICallableTaskFrameWork callableTaskFrameWorkPublish = new CallableTaskFrameWork();
        List<CallableTemplate<OneNetPublishModel>> tasksPublish = new LinkedList<CallableTemplate<OneNetPublishModel>>();
        PublishThreadHandler publishThreadHandler = null;
        for (OneNetPublishModel oneNetPublishModel : results) {
            if (oneNetPublishModel != null) {
                publishThreadHandler = new PublishThreadHandler(oneNetPublishModel);
                tasksPublish.add(publishThreadHandler);
            }
        }


        //等待30s，等待时长可自定义。用于查看日志统计时长。
        for (int time = params.getWait(); time >= 0; time--) {
            System.out.println(time + "秒后执行上传数据");
            Thread.sleep(1000);
        }

        //开始时间
        start = Instant.now();
        //创建线程池
        List<OneNetPublishModel> resultsPublis = callableTaskFrameWorkPublish
                .submitsAll(tasksPublish, corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingDeque<Runnable>());
        logger.info("上传所有数据点执行完毕!");
        //结束时间
        finish = Instant.now();
        //计算总耗时
        timeElapsed = Duration.between(start, finish).toMillis();
        //上传数据点总耗时
        resultMap.put("publishTimeAll", timeElapsed);
        //上传结果列表
        resultsPublis.removeAll(Collections.singleton(null));
        resultMap.put("resultsPublis", resultsPublis);
        for (OneNetPublishModel oneNetPublishModel : results) {
            oneNetPublishModel.getConnection().disconnect();
        }
        return resultMap;
    }

    /**
     * 把一个字符串转换成bean对象
     *
     * @param str
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    class ConnectionThreadHandler extends CallableTemplate<OneNetPublishModel> {
        private String deviceName;
        private String data;
        private Integer count;

        public ConnectionThreadHandler(String deviceName, String data, Integer count) {
            this.deviceName = deviceName;
            this.data = data;
            this.count = count;
        }

        @Override
        public OneNetPublishModel process() throws Exception {
            try {
                //创建mqtt客户端
                MQTT mqtt = new MQTT();
                mqtt.setHost(HOST_URL, HOST_PORT);
                mqtt.setClientId(deviceName);
                mqtt.setVersion(VERSION);
                mqtt.setUserName(productId);
                mqtt.setPassword(OneNetTokenUtil.generateDefaultProductToken(productId, accesskey));
                //开始时间
                Instant start = Instant.now();
                BlockingConnection connection = mqtt.blockingConnection();
                //连接mqtt
                connection.connect();
                //结束时间
                Instant finish = Instant.now();
                //计算连接耗时
                long timeElapsed = Duration.between(start, finish).toMillis();
                logger.info("设备已连接,deviceName={}", deviceName);
                //保持mqtt链接
                connection.isConnected();

                return new OneNetPublishModel()
                        .setConnection(connection)
                        .setDeviceName(deviceName)
                        .setProductId(productId)
                        .setPublishTime(timeElapsed)
                        .setCount(count)
                        .setData(data);
            } catch (Exception e) {
                logger.error("设备链接失败", e);
                return null;
            }


        }
    }


    class PublishThreadHandler extends CallableTemplate<OneNetPublishModel> {
        private OneNetPublishModel oneNetPublishModel;

        public PublishThreadHandler(OneNetPublishModel oneNetPublishModel) {
            this.oneNetPublishModel = oneNetPublishModel;
        }

        @Override
        public OneNetPublishModel process() throws Exception {
            /**
             * 组装OneNET上报数据点topic
             * https://open.iot.10086.cn/doc/mqtt/book/device-develop/topics/dp-topics.html
             */
            try {


                String topic = "$sys/" + oneNetPublishModel.getProductId() + "/" + oneNetPublishModel.getDeviceName() + "/dp/post/json";
                //模拟上报数据,格式参考documents项目下相关协议

                //重置耗时
                oneNetPublishModel.setPublishTime(0);
                //开始时间
                Instant start = Instant.now();
                //mqtt数据点上传
                JSONObject jsonObject = JSONUtil.parseObj(URLDecoder.decode(oneNetPublishModel.getData(),"UTF-8"));
                JSONArray array = new JSONArray();
                for (int i = 0; i < oneNetPublishModel.getCount(); i++) {
                    JSONObject object = jsonObject.getJSONObject("dp").getJSONArray("deviceReport").getJSONObject(0);
                    object.getJSONObject("v").getJSONArray("reports").getJSONObject(0).set("sts1", String.valueOf(System.currentTimeMillis() / 1000));
                    Snowflake snowflake = IdUtil.getSnowflake(1, 1);
                    long id = snowflake.nextId();
                    object.getJSONObject("v").getJSONArray("reports").getJSONObject(0).set("cfg16", Integer.parseInt(String.valueOf(id).substring(10, 19)));
                    array.add(new JSONObject(object.toString()));
                }
                jsonObject.getJSONObject("dp").remove("deviceReport");
                jsonObject.getJSONObject("dp").set("deviceReport", array);
                oneNetPublishModel.getConnection().publish(topic, jsonObject.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                oneNetPublishModel.getConnection().disconnect();

                //结束时间
                Instant finish = Instant.now();
                //计算耗时
                long timeElapsed = Duration.between(start, finish).toMillis();
                oneNetPublishModel.setPublishTime(timeElapsed);
                //关闭链接
                oneNetPublishModel.getConnection().disconnect();
                logger.info("设备devicename={}上传数据", oneNetPublishModel.getDeviceName(), timeElapsed);
                return oneNetPublishModel;
            } catch (Exception e) {
                logger.info("设备devicename={}上传数据失败", oneNetPublishModel.getDeviceName(), e);
                oneNetPublishModel.getConnection().disconnect();
                return null;
            }
        }
    }


    /**
     * 获取表格文件中的imei列表
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    private List<String> getExcelModeListBytes(byte[] bytes) throws IOException {
        Sheet sheet = new Sheet(1, 1, ExcelMode.class);
        // 这里 取出来的是 ExcelModel实体 的集合
        List<Object> readList = EasyExcelFactory.read(new ByteArrayInputStream(bytes), sheet);
        // 存 ExcelMode 实体的 集合
        List<String> list = new ArrayList<String>();
        for (Object obj : readList) {
            ExcelMode excelMode = (ExcelMode) obj;
            if (excelMode.getImei() != null) {
                if (!excelMode.getImei().isEmpty()) {
                    list.add(((ExcelMode) obj).getImei());
                }
            }
        }
        return list;
    }
}
