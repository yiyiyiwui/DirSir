package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.BusinessException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;



    /*菜品新增*/
    @Override
    public void saveDishWithFlavor(DishDTO dishDTO) {
        //1 参数校验
        if (StrUtil.isBlank(dishDTO.getName())
                || dishDTO.getCategoryId()==null
                || dishDTO.getPrice()==null
                || StrUtil.isBlank(dishDTO.getImage())) {
            throw new BusinessException("不能为空");
        }
        //2 业务校验
        Dish oldDish = dishMapper.getByName(dishDTO.getName());
        if (oldDish!=null) {
            throw new BusinessException("菜品已存在");
        }
        //3 菜品dto转菜品entity
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        //4 补充状态
        //默认是禁用
        dish.setStatus(StatusConstant.DISABLE);
        //5 调用dishMapper新增（主键返回）
        log.info("菜品新增前id:{}",dish.getId());
        dishMapper.insert(dish);
        log.info("菜品新增后id:{}",dish.getId());
        //6 取出口味列表
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        //7 遍历（非空判断）
        if (ArrayUtil.isNotEmpty(flavorList)) {
            for (DishFlavor dishFlavor : flavorList) {
                //关联菜品id
                dishFlavor.setDishId(dish.getId());
                //保存口味
                dishFlavorMapper.insert(dishFlavor);
            }

        }

    }

    /*菜品分页*/
    @Override
    public PageResult getPage(DishPageDTO dishPageDTO) {
        //1 开启分页
        PageHelper.startPage(dishPageDTO.getPage(), dishPageDTO.getPageSize());
        //2 查询list
        List<DishVO> voList = dishMapper.getList(dishPageDTO);
        Page<DishVO> page = (Page<DishVO>) voList;
        //3 返回分页
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*菜品回显*/
    @Override
    public DishVO getById(Long id) {
        //1 根据id查询菜品
        Dish dish = dishMapper.getById(id);
        //2 根据菜品id查询口味列表
        List<DishFlavor> flavors = dishFlavorMapper.getListByDishId(id);
        //3 封装vo
        DishVO dishVO = BeanUtil.copyProperties(dish, DishVO.class);
        dishVO.setFlavors(flavors);
        //4 返回vo
        return dishVO;
    }

    /*修改菜品*/
    @Override
    public void updateById(DishDTO dishDTO) {
        //1 修改菜品基本信息
        //1.1 菜品dto转菜品entity
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        //1.2 调用mapper修改
        dishMapper.updateById(dish);
        //2 根据菜品id删除旧口味列表
        dishFlavorMapper.deleteByDishId(dish.getId());
        //3 遍历新口味列表
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (ArrayUtil.isNotEmpty(flavorList)) {
            for (DishFlavor dishFlavor : flavorList) {
            //3.1 关联菜品id
                dishFlavor.setDishId(dish.getId());
            //3.2 保存口味
                dishFlavorMapper.insert(dishFlavor);
            }
        }



    }
    @Autowired
    SetmealDishMapper setmealDishMapper;

    /*删除菜品*/
    @Override
    public void deleteBatch(List<Long> ids) {
        //1 先查询菜品售卖状态
        for (Long id : ids) {
            if (dishMapper.getById(id).getStatus().equals(StatusConstant.ENABLE)) {
                //只要查询到有一个在售卖中，就抛异常
                throw new BusinessException("有菜品在起售，不能删除");
            }
        }
        //2 查询菜品是否有关联的套餐
        Integer countSetmeal = setmealDishMapper.countByDishIds(ids);
        if (countSetmeal>0) {
            //有一个关联套餐，也不可删除
            throw new BusinessException("有关联套餐菜品不可删除");
        }
        ids.forEach(id->{
        //3 删除菜品基本信息
            dishMapper.deleteById(id);
        //4 删除菜品口味列表
            dishFlavorMapper.deleteByDishId(id);
        });

    }

    @Autowired
    private SetmealMapper setmealMapper;

    /*启用禁用*/
    @Override
    public void startOrStop(Integer status, Long id) {
        //1 封装实体
        Dish dish = Dish.builder().id(id).status(status).build();
        //2 修改菜品状态
        dishMapper.updateById(dish);
        //3 判断是否为禁用
        if (status.equals(StatusConstant.DISABLE)) {
            //同时禁用关联的套餐
            setmealMapper.updateStatusByDishId(id);
        }
    }

    /*查询菜品列表*/
    @Override
    public List<DishVO> getList(DishPageDTO dishPageDTO) {
        return dishMapper.getList(dishPageDTO);
    }
}
