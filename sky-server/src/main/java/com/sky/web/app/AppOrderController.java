package com.sky.web.app;

import com.sky.annotation.AutoFill;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor // lombok 代替 @Autowired
@RestController
@RequestMapping("/user/order")
@Slf4j
public class AppOrderController {

    // 必须加 final
    private final OrderService orderService;

    // 提交订单
    @PostMapping("/submit")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO vo = orderService.submit(ordersSubmitDTO);
        return Result.success(vo);
    }

    // 订单支付
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /*历史订单*/
    @GetMapping("/historyOrders")
    public Result historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /*订单详情*/
    @GetMapping("/orderDetail/{id}")
    public Result orderDetail(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    /*取消订单*/
    @PutMapping("cancel/{id}")
    public Result cancel(@PathVariable("id") Long id) {
        orderService.cacel(id);
        return Result.success();
    }

    /*再来一单*/
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable("id") Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    /*催单*/
    @GetMapping("reminder/{id}")
    public Result reminder(@PathVariable("id") Long id) {
        orderService.reminder(id);
        return Result.success();
    }
}
