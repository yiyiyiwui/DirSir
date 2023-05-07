package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.interfaces.PBEKey;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /*每分钟处理一次订单超时时间*/
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("开始清理超时订单");
        //获取15分钟开始前的时间
        LocalDateTime old15Time = LocalDateTime.now().plusMinutes(-15);
        //查询未支付的订单
        List<Orders> weizhifuList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, old15Time);
        //遍历出所有的状态为未支付订单。然后修改为已取消
        weizhifuList.forEach(weizhifu -> { //遍历
            weizhifu.setStatus(Orders.CANCELLED);// 修改状态
            weizhifu.setCancelTime(LocalDateTime.now()); // 记录当前时间
            weizhifu.setCancelReason("用户超时未支付"); // 订单取消原因
            orderMapper.update(weizhifu);// 调用mapper修改
            log.info("清理超时订单号为：" + weizhifu.getNumber());
        });
        log.info("结束清理超时订单");
    }

    /*每天凌晨1点处理配送中的订单*/
    @Scheduled(cron = "0 0 1 * * ?")
    public void processToOver() {
        log.info("开始处理配送订单");
        //获取到1小时前的时间
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);
        //查询还在派送中的订单
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        //遍历处所有状态为派送中的订单，修改为已完成
        for (Orders orders : byStatusAndOrderTimeLT) {
            orders.setStatus(Orders.COMPLETED);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("清理前一天的缓存");
            orderMapper.update(orders);
            log.info("处理配送订单号为：" + orders.getNumber());
        }
        log.info("结束处理配送订单");
    }

    /*1分钟之后上传双十一优惠套餐*/
//    @Scheduled(cron = "11 22 15 6 5 ? 2023")
//    public void show() {
//    }
}
