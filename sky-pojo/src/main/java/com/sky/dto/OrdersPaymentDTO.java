package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
//订单付款
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;

}
