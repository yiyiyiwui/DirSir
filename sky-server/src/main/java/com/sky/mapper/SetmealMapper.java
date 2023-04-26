package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {
    /*根据分类id统计套餐数量*/
    @Select("select count(*) from setmeal where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
