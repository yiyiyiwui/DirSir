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

    /*新增用户数量*/
    @Select("select count(id) from user where create_time > #{beginTime} and create_time < #{endTime}")
    Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime);

    /*查询每天有效订单数*/
    @Select("select count(id) from orders where order_time > #{beginTime} and order_time < #{endTime} and status = #{completed}")
    Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer completed);

    /*销量排名*/
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}