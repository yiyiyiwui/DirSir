package com.sky.web.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /*修改菜品*/
    @PutMapping
    public Result updateById(@RequestBody DishDTO dishDTO) {
        dishService.updateById(dishDTO);
        return Result.success();
    }

    /*删除菜品*/
    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids) {
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /*启用禁用*/
    @PostMapping("/status/{status}")
    public Result startOrstop(@PathVariable("status") Integer status, @RequestParam Long id) {
        dishService.startOrStop(status, id);
        return Result.success();
    }

    /*查询菜品列表*/
    @GetMapping("/list")
    public Result getList(DishPageDTO dishPageDTO) {
        List<DishVO> voList = dishService.getList(dishPageDTO);
        return Result.success(voList);
    }
}
