package com.nice.mqtt.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author hzdz163@163.com
 * @Description 表格解析实体类
 * @Date 22:53 2020/9/11
 * @Param
 * @return
 **/
@Data
@ToString
public class ExcelMode extends BaseRowModel implements Serializable {

    /**
     * 第一列的数据
     */
    @ExcelProperty(index = 0)
    private String imei;
    /**
     * 第二列的数据
     */
    @ExcelProperty(index = 1)
    private String column2;

}
