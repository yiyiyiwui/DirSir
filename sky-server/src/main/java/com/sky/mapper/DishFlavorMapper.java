package com.sky.mapper;

import com.sky.dto.DishPageDTO;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /*新增口味*/
    void insert(DishFlavor dishFlavor);

    /*根据id查询口味列表*/
    List<DishFlavor> getListByDishId(Long id);

    /*根据菜品id删除口味*/
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long id);


}
