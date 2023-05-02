package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /*查询购物车列表*/
    @Override
    public List<ShoppingCart> getList() {
        //1 取出登陆人
        Long userId = ThreadLocalUtil.getCurrentId();
        //2 调用mapper查询
        return shoppingCartMapper.getByUserId(userId);
    }

    /*添加购物车*/
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {
        //1 dto转成entity
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        //2 把登陆人id设置到entity
        shoppingCart.setUserId(ThreadLocalUtil.getCurrentId());
        //3 条件查询是否有此购物项
        ShoppingCart oldShoppingCart = shoppingCartMapper.getOne(shoppingCart);
        //4 如果有，数量就+1 然后更新到数据库
        if (oldShoppingCart != null) {
            oldShoppingCart.setNumber(oldShoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumber(oldShoppingCart);
        } else {
        //5 如果没有，菜品新增，信息补全，保存到到数据库   套餐新增，信息补全，保存到数据库
            if (shoppingCart.getDishId() != null) {
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());// 菜品
                shoppingCart.setImage(dish.getImage()); // 图片
                shoppingCart.setAmount(dish.getPrice());// 单价
            } else {
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());//菜品
                shoppingCart.setImage(setmeal.getImage());//图片
                shoppingCart.setAmount(setmeal.getPrice());//单价
            }
            //不管是菜品还是套餐都需要数量+1和添加时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now()); // 添加时间
            shoppingCartMapper.insert(shoppingCart);

        }
    }

    /*清空购物*/
    @Override
    public void clean() {
        //1 获取登录用户id
        Long userId= ThreadLocalUtil.getCurrentId();
        //2 调用mapper清理
        shoppingCartMapper.clean(userId);
    }

    /*删除购物车其中一个商品*/
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        ShoppingCart oldshoppingCart = shoppingCartMapper.getOne(shoppingCart);
        //删除购物项或者数量
        if (oldshoppingCart!=null) {
            Integer number= oldshoppingCart.getNumber();
            if (number == 1) { //如果当前商品再购物车中个数为一，直接删除当前记录
                shoppingCartMapper.deleteById(oldshoppingCart.getId());
            } else {//当前商品再购物车中的个数不为1，则只需要修改个数就可以
                oldshoppingCart.setNumber(oldshoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumber(oldshoppingCart);
            }
        }




    }

}
