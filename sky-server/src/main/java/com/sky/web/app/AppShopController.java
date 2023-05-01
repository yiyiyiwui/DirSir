package com.sky.web.app;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class AppShopController {

    @Autowired
    private RedisTemplate redisTemplate ;

    /*查询店铺状态*/
    @GetMapping("status")
    public Result getShopStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(status);//店铺状态 1 为营业 0为打烊
    }
}
