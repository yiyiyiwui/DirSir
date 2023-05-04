package com.sky.baidu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author MYM_ZUOYAN
 * @version 2.0
 * @date 2021/4/9 15:03
 * 位置信息返回实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnLocationBean implements Serializable {

    private static final Long serializableValue = 1L;


    /**
     * 地理位置
     */
    private String formattedAddress;


    /**
     * 经度
     */
    private Double lng;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 品级
     */
    private String level;
}
