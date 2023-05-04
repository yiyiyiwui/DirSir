package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
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

    /*订单搜索*/
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /*订单数量统计*/
    OrderStatisticsVO statistics();

    /*接单*/
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /*拒单*/
    void finm(OrdersConfirmDTO ordersConfirmDTO);

    /*取消订单*/
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /*派送订单*/
    void delivery(Long id);

    /*完成订单*/
    void complete(Long id);
}