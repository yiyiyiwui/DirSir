package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /*新增套餐*/
    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        // 1 参数校验
        if (StrUtil.isBlank(setmealDTO.getName()) || setmealDTO.getPrice()==null || setmealDTO.getImage()==null || setmealDTO.getSetmealDishes()==null) {
            throw new BusinessException("非法参数");
        }
        //2 业务校验
        SetmealDTO setmealDTO1 = setmealMapper.getByName(setmealDTO.getName());
        if (setmealDTO1!=null) {
            throw new BusinessException("套餐已存在");
        }
        //3 dtoz转entity
        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmeal.setStatus(StatusConstant.DISABLE);//默认禁用
        //4 保存套餐基本信息
        setmealMapper.insert(setmeal);
        //5 取出中间表
        List<SetmealDish> dishList = setmealDTO.getSetmealDishes();
        if (ArrayUtil.isNotEmpty(dishList)) {
            for (SetmealDish setmealDish : dishList) {
           //5.1 关联套餐id
            setmealDish.setSetmealId(setmeal.getId());
           //5.2 保存中间表
            setmealDishMapper.insert(setmealDish);
            }
        }
    }

    /*套餐分页*/
    @Override
    public PageResult getPage(SetmealPageDTO setmealPageDTO) {
        //1 开启分页
        PageHelper.startPage(setmealPageDTO.getPage(), setmealPageDTO.getPageSize());
        //2 查询list(条件)
        List<SetmealDTO> volist = setmealMapper.getList(setmealPageDTO);
        Page<SetmealDTO> page = (Page<SetmealDTO>) volist;
        //3  返回分页
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*回显套餐*/
    @Override
    public SetmealVO getById(Long id) {
        //1 先查询套餐基本信息
        Setmeal setmeal = setmealMapper.getById(id);
        //2 然后查询中间表信息
        List<SetmealDish> dishList = setmealDishMapper.getListBySetmealId(id);
        //3 封装vo
        SetmealVO setmealVO = BeanUtil.copyProperties(setmeal, SetmealVO.class);
        setmealVO.setSetmealDishes(dishList);
        //4 返回vo
        return setmealVO;
    }

    /*修改套餐*/
    @Override
    @Transactional
    public void updateById(SetmealDTO setmealDTO) {
        //1 先修改套餐
        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmealMapper.updateById(setmeal);
        //2 删除旧的菜品
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //3 添加新的菜品
        setmealDTO.getSetmealDishes().forEach(setmealDish -> {
            setmealDish.setDishId(setmeal.getId());
            setmealDishMapper.insert(setmealDish);
        });
    }

    /*批量删除套餐*/
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //1 起售中的套餐不能删除
        ids.forEach(id->{
            Setmeal setmeal = setmealMapper.getById(id);
            if (StatusConstant.ENABLE == setmeal.getStatus()) {
                throw new BusinessException("起售中的套餐不能删除");
            }
        });
        ids.forEach(setmealId->{
            //2 删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //3 删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }
    @Autowired
    private DishMapper dishMapper;

    /*起售停售套餐*/
    @Override
    @Transactional
    public void startOrStop(Setmeal setmeal) {
        //1 起售套餐：需要查看关联菜品是否都是起售
        if (setmeal.getStatus()== StatusConstant.ENABLE) {
        //2 查询是否关联有停售状态菜品
            Long count = dishMapper.countBySidAndStatus(setmeal.getId(), 0);
            if (count != null && count > 0) {
                throw new BusinessException("套餐关联有未起售菜品，套餐不能起售");
            }
        }
        //3 修改套餐状态
        setmealMapper.updateById(setmeal);
    }
}
