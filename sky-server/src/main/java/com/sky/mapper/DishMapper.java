package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
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

    /*修改菜品*/
    @AutoFill("update")
    void updateById(Dish dish);

    /*删除菜品*/
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);

    /*起售停售套餐*/
    @Select("select count(*) from dish where status=#{status} and id in(select dish_id from setmeal_dish where setmeal_id =#{setmealId}) ")
    Long countBySidAndStatus(Long setmealId, int status);
}
