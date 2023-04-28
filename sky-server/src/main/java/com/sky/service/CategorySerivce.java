package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional//事务
public interface CategorySerivce {
    /*分类分页*/
    PageResult getPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /*新增分类*/
    void save(CategoryDTO categoryDTO);

    /*删除分类*/
    void deleteById(Long id);

    /*根据类型查询分类*/
    List<Category> getList(Integer type);

    /*根据id修改分类*/
    void updateById(Category category);

    /*分类启用禁用*/
    void startOrStop(Integer status, Long id);
}
