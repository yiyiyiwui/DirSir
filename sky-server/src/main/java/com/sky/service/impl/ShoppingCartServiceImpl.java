package com.sky.service.impl;

import com.sky.context.ThreadLocalUtil;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    /*查询购物车列表*/
    @Override
    public List<ShoppingCart> getList() {
        //1 取出登陆人
        Long userId = ThreadLocalUtil.getCurrentId();
        //2 调用mapper查询
        return shoppingCartMapper.getByUserId(userId);
    }

}
