package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 提交订单
    void insert(Orders orders);

    // 根据订单号查询
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    // 修改订单信息
    void update(Orders orders);

    /*条件查询订单列表*/
    List<OrderVO> getList(OrdersPageQueryDTO ordersPageQueryDTO);

    /*订单详情，根据id查询订单*/
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /*修改订单状态*/
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /*查询指定状态和时间的营业额*/
    Double sumTurnover(Integer status, LocalDate currentDate);

    /*统计订单数量*/
    Integer countOrder(Integer status, LocalDate currentDate);

    /*查询时间区间内的销量排名*/
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}