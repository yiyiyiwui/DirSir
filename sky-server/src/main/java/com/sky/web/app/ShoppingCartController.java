package com.sky.web.app;

import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.CategorySerivce;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /*查询购物车列表*/
    @GetMapping("/list")
    public Result getList() {
         List<ShoppingCart> list = shoppingCartService.getList();
        return Result.success(list);
    }
}
