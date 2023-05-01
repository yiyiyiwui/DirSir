package com.sky.web.admin;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    /*设置店铺状态*/
    @PostMapping("/{status}")
    public Result setStatus(@PathVariable("status") Integer status) {
        //调用redis设置
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("SHOP_STATUS", status);
        return Result.success();
    }
}
