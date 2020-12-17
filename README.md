# nicemqtt
## 模拟批量mqtt客户端连接onenet并上报数据，可用于模拟设备上线、收发数据、压力测试、业务测试等
## 准备
#### 1.需了解mqtt和onenet基础知识，了解onenet相关api的调用方式。
#### 2.需要批量模拟的设备需要提前在onnet平台中进行批量注册。
#### 3.可以使用程序自带生成token的工具类，也可自定义token获取的方式，如http等。
## 使用
####  1.在 nicemqtt/src/main/resources/application.yml 中指定productId 和 accessKey，目的是为了生成token，指定后端项目运行在localhost的8080端口。
####  2.解压目录中的 nice-mqtt.zip，运行其中的 nice-mqtt.exe。
####  3.在程序中填写相关数据，并上传device列表文件，格式为xls。内容与onenet批量注册设备时的模板文件保持一致即可。
####  4.上传成功后，即可跳转到结果看板。
## 提示
### 如需帮助或获取exe源码 -> hzdz163@163.com
[![r8FPQU.md.png](https://s3.ax1x.com/2020/12/17/r8FPQU.md.png)](https://imgchr.com/i/r8FPQU)
[![r8Fw6S.png](https://s3.ax1x.com/2020/12/17/r8Fw6S.png)](https://imgchr.com/i/r8Fw6S)
