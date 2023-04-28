package com.sky.web.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategorySerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.interfaces.PBEKey;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/category")//springmvc的方法，用来给当前地址绑定一个url的网络地址
public class CategoryController {
    @Autowired
    CategorySerivce categorySerivce;

    /*分类分页*/
    @GetMapping("page")
    public Result getPage( CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categorySerivce.getPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /*新增分类*/

    @PostMapping /*@RequestBody 将方法的返回值转为json，然后响应到客户端*/
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        //调用service新增
        categorySerivce.save(categoryDTO);
        //返回
        return Result.success();
    }

    /*删除分类*/
    @DeleteMapping
    public Result deleteById(@RequestParam Long id) {
        categorySerivce.deleteById(id);
        return Result.success();
    }

    /*根据类型查询分类*/
    @GetMapping("/list")
    public Result getList(Integer type) {
        List<Category> list = categorySerivce.getList(type);
        return Result.success(list);
    }

    /*根据id修改分类*/
    @PutMapping
    public Result updateById(@RequestBody Category category) {
        categorySerivce.updateById(category);
        return Result.success();
    }

    /*分类启用禁用*/
    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, @RequestParam Long id) {
        categorySerivce.startOrStop(status, id);
        return Result.success();
    }
}
