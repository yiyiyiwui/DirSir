package com.sky.web.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /*新增套餐*/
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /*套餐分页*/
    @GetMapping("/page")
    public Result getPage(SetmealPageDTO setmealPageDTO) {
        PageResult pageResult = setmealService.getPage(setmealPageDTO);
        return Result.success(pageResult);
    }

    /*回显套餐*/
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Long id) {
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /*修改套餐*/
    @PutMapping
    public Result updateById(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateById(setmealDTO);
        return Result.success();
    }

    /*套餐批量删除*/
    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /*起售停售套餐*/
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, @RequestParam Long id) {
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealService.startOrStop(setmeal);
        return Result.success();
    }

}
