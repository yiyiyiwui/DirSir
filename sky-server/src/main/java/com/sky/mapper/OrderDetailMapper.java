package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    // 新增订单明细
    void insert(OrderDetail orderDetail);

    /*根据订单id查询明细列表*/
    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getByOrderId(Long id);
}