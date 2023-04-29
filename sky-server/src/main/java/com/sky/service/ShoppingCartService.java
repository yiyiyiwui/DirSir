package com.sky.service;

import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ShoppingCartService {
    /*查询购物车列表*/
    List<ShoppingCart> getList();
}
