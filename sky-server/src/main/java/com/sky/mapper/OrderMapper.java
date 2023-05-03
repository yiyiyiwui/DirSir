package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
}