package com.sky.service.impl;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService  {
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    /*今日数据*/
    @Override
    public BusinessDataVO getBusinessData(LocalDate currentDate) {
        // 查询今天新增用户数
        Integer integer = userMapper.countUser(currentDate);
        // 查询营业额
        Double turnover = orderMapper.sumTurnover(Orders.COMPLETED, currentDate);
        if (turnover == null) {
            turnover = 0.0;
        }
        // 查询今天总订单
        Integer countOrder = orderMapper.countOrder(null, currentDate);
        // 查询今天有效订单数
        Integer order = orderMapper.countOrder(Orders.COMPLETED, currentDate);
        // 查询订单完成率
        Double orderCompletionRate = 0.0;
        if (countOrder>0 && order>0) {
            orderCompletionRate=order*1.0/countOrder;
        }
        // 查询平均客单价
        Double unitPrice =0.0;
        if (turnover>0 && order>0) {
            unitPrice=turnover/order;
        }
        // 保留两位小数  第一种方法
        String format = new DecimalFormat("#.00").format(unitPrice);
        Double aDouble = Double.valueOf(format);

        //第二种方法
        String str22 = String.format("%.2f", unitPrice);
        unitPrice = Double.parseDouble(str22);
        // 封装返回vo
        return BusinessDataVO.builder()
                .newUsers(integer)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(order)
                .build();
    }

    /*订单管理*/
    @Override
    public Object getOverviewOrders(LocalDate localDate) {


        //1 获取全部订单
        Integer countOrder = orderMapper.countOrder(null, localDate);
        //2 已取消订单数量
        Integer cancelOrder = orderMapper.countOrder(Orders.CANCELLED, localDate);
        //3 已完成订单数量
        Integer okOrder = orderMapper.countOrder(Orders.COMPLETED, localDate);
        //4 待派送订单数量
        Integer delivereOrders = orderMapper.countOrder(Orders.CONFIRMED, localDate);
        //5 待接单订单数量
        Integer waitingOrders = orderMapper.countOrder(Orders.TO_BE_CONFIRMED, localDate);
        // 返回vo

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.set()

        return OrderOverViewVO.builder().allOrders(cancelOrder).cancelledOrders(cancelOrder).deliveredOrders(delivereOrders).waitingOrders(waitingOrders).build();

    }



}
