package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional//事务
public interface DishService {
    /*菜品新增*/
    void saveDishWithFlavor(DishDTO dishDTO);

    /*菜品分页*/
    PageResult getPage(DishPageDTO dishPageDTO);

    /*菜品回显*/
    DishVO getById(Long id);

    /*修改菜品*/
    void updateById(DishDTO dishDTO);

    /*删除菜品*/
    void deleteBatch(List<Long> ids);

    /*启用禁用*/
    void startOrStop(Integer status, Long id);

    /*查询菜品列表*/
    List<DishVO> getList(DishPageDTO dishPageDTO);

    /*条件查询*/
    List<DishVO> getParamList(DishPageDTO dishPageDTO);
}
