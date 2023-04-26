package com.sky.web.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /*菜品新增*/
    @PostMapping
    public Result saveDishWithFlavor(@RequestBody DishDTO dishDTO) {
        dishService.saveDishWithFlavor(dishDTO);
        return Result.success();
    }

    /*菜品分页*/
    @GetMapping("/page")
    public Result getPage(DishPageDTO dishPageDTO) {
        PageResult pageResult = dishService.getPage(dishPageDTO);
        return Result.success(pageResult);
    }

    /*菜品回显*/
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Long id) {
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
}
