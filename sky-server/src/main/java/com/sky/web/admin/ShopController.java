package com.sky.web.admin;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Random;

@RestController
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    /*设置店铺状态*/
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable("status") Integer status) {
        //调用redis设置
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("SHOP_STATUS", status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result getStatus() {
        //调用redis设置
        Object status = redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(status);
    }
}
