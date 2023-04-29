package com.sky.web.app;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategorySerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user/category")
public class AppCategoryController {
    @Autowired
    private CategorySerivce categorySerivce;

    /*查询起售分类列表*/
    @GetMapping("/list")
    public Result getList() {
        //1 封装dto
        CategoryDTO categoryDTO = CategoryDTO.builder().status(1).build();
        //2 调用service
        List<Category> list = categorySerivce.getParamList(categoryDTO);
        //3返回
        return Result.success(list);
    }
}
