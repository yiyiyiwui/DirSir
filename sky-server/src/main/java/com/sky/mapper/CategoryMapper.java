package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /*条件查询*/
    List<Category> getList(CategoryPageQueryDTO categoryPageQueryDTO);

    /*根据名称查询*/
    @Select("select * from category where name =#{name}")
    Category getByName(String name);

    /*新增*/
    @AutoFill("insert")//自定义注解
    void insert(Category category);

    /*删除*/
    @Delete("delete from category where id=#{id}")
    void deleteById(Long id);

    /*更新*/
    void updateById(Category category);

    /*条件查询*/
    default List<Category> getParamList() {
        return getParamList();
    }

    /*条件查询*/
    List<Category> getParamList(CategoryDTO categoryDTO);
}
