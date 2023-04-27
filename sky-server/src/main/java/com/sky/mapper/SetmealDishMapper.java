package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
/*套餐菜品中间表*/
public interface SetmealDishMapper {

    /*查询菜品是否有关联的套餐*/
    Integer countByDishIds(List<Long> ids);

    /*保存中间表数据*/
    void insert(SetmealDish setmealDish);

    /*根据套餐id查询*/
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> getListBySetmealId(Long id);

    /*根据套餐id删除套餐和菜品的关联关系*/
    @Delete("delete from setmeal_dish where setmeal_id =#{setmealId}")
    void deleteBySetmealId(Long id);
}
