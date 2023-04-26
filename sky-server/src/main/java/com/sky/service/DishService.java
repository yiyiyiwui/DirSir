package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {
    /*菜品新增*/
    void saveDishWithFlavor(DishDTO dishDTO);

    /*菜品分页*/
    PageResult getPage(DishPageDTO dishPageDTO);

    /*菜品回显*/
    DishVO getById(Long id);
}
