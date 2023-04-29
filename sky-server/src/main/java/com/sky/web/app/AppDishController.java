package com.sky.web.app;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/dish")
public class AppDishController {

    @Autowired
    private DishService dishService;

    /*根据id查询菜品列表（小程序）*/
    @GetMapping("/list")
    public Result getList(@RequestParam Long categoryId) {
        //封装dto条件
        DishPageDTO dishPageDTO = DishPageDTO.builder()
                .categoryId(categoryId)
                .status(1)
                .build();
        //调用service查询，返回结果
        return Result.success(dishService.getParamList(dishPageDTO));
    }
}
