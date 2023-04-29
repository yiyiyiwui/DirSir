package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional//事务
public interface SetmealService {

    /*新增套餐*/
    void save(SetmealDTO setmealDTO);

    /*套餐分页*/
    PageResult getPage(SetmealPageDTO setmealPageDTO);

    /*回显套餐*/
    SetmealVO getById(Long id);

    /*修改套餐*/
    void updateById(SetmealDTO setmealDTO);

    /*批量删除套餐*/
    void deleteBatch(List<Long> ids);

    /*奇搜停售套餐*/
    void startOrStop(Setmeal setmeal);

    /*根据分类id查询起售的套餐列表*/
    List<SetmealVO> getParamList(SetmealPageDTO setmealPageDTO);
}
