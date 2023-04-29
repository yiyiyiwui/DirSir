package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /*查询购物车列表*/
    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> getByUserId(Long userId);

}
