package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.BusinessException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategorySerivce;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CategorySerivceImpl implements CategorySerivce {
    @Autowired
     private CategoryMapper categoryMapper;
    /*分类分页*/
    @Override
    public PageResult getPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        //1 开启分页
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //2 查询list
        List<Category> list = categoryMapper.getList(categoryPageQueryDTO);
        Page<Category> page = (Page<Category>) list;
        //3 返回分页对象
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*新增分类*/
    @Override
    public void save(CategoryDTO categoryDTO) {
        //1 参数校验
        if (StrUtil.isBlank(categoryDTO.getName()) || categoryDTO.getType()==null || categoryDTO.getSort()==null) {
            throw new BusinessException("非法参数");
        }
        //2 业务校验
        Category oldCategory = categoryMapper.getByName(categoryDTO.getName());
        if (oldCategory!=null) {
            throw new BusinessException("分类已存在");
        }
        //3 dto转entity
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        //4 补充信息/标注了自定义注解后这里可以不写
          category.setStatus(StatusConstant.DISABLE);//设置新增为禁用
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(ThreadLocalUtil.getCurrentId());
//        category.setUpdateUser(ThreadLocalUtil.getCurrentId());
        //5 调用mapper新增
        categoryMapper.insert(category);
    }

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /*删除分类*/
    @Override
    public void deleteById(Long id) {
        //1 先根据id统计菜品数量
        Integer dishCount = dishMapper.countByCategoryId(id);
        if (dishCount>0) {
            throw new BusinessException("分类下有菜品，无法删除");
        }
        //2 根据分类id统计套餐数量
        Integer setmealCount = setmealMapper.countByCategoryId(id);
        if (setmealCount>0) {
            throw new BusinessException("分类下有套餐，无法删除");
        }
        //3 调用mapper删除
        categoryMapper.deleteById(id);
    }

    /*根据类型查询分类*/
    @Override
    public List<Category> getList(Integer type) {
        //1 将type封装到dto
        CategoryPageQueryDTO categoryPageQueryDTO = new CategoryPageQueryDTO();
        categoryPageQueryDTO.setType(type);
        //2 调用mapper查询并返回
        return categoryMapper.getList(categoryPageQueryDTO);
    }

    /*根据id修改分类*/
    @Override
    public void updateById(Category category) {
        //1 设置修改人
        category.setUpdateUser(ThreadLocalUtil.getCurrentId());
        //2 设置最后更新时间
        category.setUpdateTime(LocalDateTime.now());
        //3 调用mapper修改
        categoryMapper.updateById(category);
    }

    /*分类启用禁用*/
    @Override
    public void startOrStop(Integer status, Long id) {
        // 封装对象，补全信息
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(ThreadLocalUtil.getCurrentId())
                .build();
        categoryMapper.updateById(category);
    }
}
