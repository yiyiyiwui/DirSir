package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ShoppingCartService {
    /*查询购物车列表*/
    List<ShoppingCart> getList();

    /*添加购物车*/
    void save(ShoppingCartDTO shoppingCartDTO);

    /*清空购物车*/
    void clean();

    /*删除购物车其中一个商品*/
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
