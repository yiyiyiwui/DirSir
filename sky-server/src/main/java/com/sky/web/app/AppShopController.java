package com.sky.web.app;

import com.sky.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class AppShopController {

    /*查询店铺状态*/
    @GetMapping("status")
    public Result getShopStatus() {
        return Result.success(1);//店铺状态 1 为营业 0为打烊
    }
}
