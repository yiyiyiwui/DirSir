package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /*根据分类id统计菜品数量*/
    @Select("select count(*) from dish where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*根据菜品名称查询*/
    @Select("select * from dish where name=#{name}")
    Dish getByName(String name);

    /*新增菜品*/
    @AutoFill("insert")
    void insert(Dish dish);

    /*菜品分页*/
    List<DishVO> getList(DishPageDTO dishPageDTO);

    /*根据id查询*/
    Dish getById(Long id);
}
