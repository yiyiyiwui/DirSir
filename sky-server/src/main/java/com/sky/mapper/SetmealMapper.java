package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {


    /*根据分类id统计套餐数量*/
    @Select("select count(*) from setmeal where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*根据菜品id修改套餐状态*/
    @Update("update setmeal set status=0 where id in(select setmeal_id from setmeal_dish where dish_id=#{dishId})")
    void updateStatusByDishId(Long id);

    /*保存套餐*/
    void insert(Setmeal setmeal);

    /*新增套餐*/
    @Select("select * from setmeal where name =#{name}")
    SetmealDTO getByName(String name);

    /*套餐分页（查询list 条件）*/
    List<SetmealVO> getList(SetmealPageDTO setmealPageDTO);

    /*根据id查询*/
    @Select("select * from setmeal where id =#{id}")
    Setmeal getById(Long id);

    /*修改套餐*/
    @AutoFill("update")
    void updateById(Setmeal setmeal);

    /*根据id删除套餐*/
    @Delete("delete from setmeal where id=#{id}]")
    void deleteById(Long setmealId);
}
