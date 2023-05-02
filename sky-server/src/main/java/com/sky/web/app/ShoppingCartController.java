package com.sky.web.app;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.CategorySerivce;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /*添加购物车*/
    @PostMapping("/add")
    public Result save(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.save(shoppingCartDTO);
        return Result.success();
    }

    /*清空购物车*/
    @DeleteMapping("/clean")
    public Result clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    /*删除购物车其中一个商品*/
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
