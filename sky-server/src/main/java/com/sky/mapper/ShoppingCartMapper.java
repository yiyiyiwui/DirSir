package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /*查询购物车列表*/
    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> getByUserId(Long userId);

    /*条件查询*/
    ShoppingCart getOne(ShoppingCart shoppingCart);

    /*更新购物车数量*/
    void updateNumber(ShoppingCart oldShoppingCart);

    /*添加购物项*/
    void insert(ShoppingCart shoppingCart);

    /*清空购物车*/
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clean(Long userId);

    /*删除购物车其中一个商品*/
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
