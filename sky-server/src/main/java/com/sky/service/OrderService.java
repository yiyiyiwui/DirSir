package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    // 提交订单
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    // 订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;


    /*历史订单*/
    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /*订单详情*/
    OrderVO orderDetail(Long id);

    /*取消订单*/
    void cacel(Long id);

    /*再来一单*/
    void repetition(Long id);

    /*催单*/
    void reminder(Long id);
}