package com.sky.web.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;

@RequiredArgsConstructor // lombok 代替 @Autowired
@RestController
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;

    /*订单搜索*/
    @GetMapping("/conditionSearch")
    public Result conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /*订单数量统计*/
    @GetMapping("/statistics")
    public Result statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /*查询订单详情*/
    @GetMapping("/details/{id}")
    public Result details(@PathVariable("id") Long id) {
        OrderVO vo = orderService.orderDetail(id);
        return Result.success(vo);
    }

    /*接单*/
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /*拒单*/
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.finm(ordersConfirmDTO);
        return Result.success();
    }

    /*取消订单*/
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /*派送订单*/
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable("id") Long id) {
        orderService.delivery(id);
        return Result.success();
    }

    /*完成订单*/
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
