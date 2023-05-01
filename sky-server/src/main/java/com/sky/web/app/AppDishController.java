package com.sky.web.app;

import com.aliyuncs.kms.model.v20160120.DisableKeyRequest;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dish")
@Slf4j
public class AppDishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*根据id查询菜品列表（小程序）*/
    @GetMapping("/list")
    public Result getList(@RequestParam Long categoryId) {

        List<DishVO> voList =null;
        //先查redis中是否有该分类的菜品缓存
        String dishkey = "DISH:"+categoryId;
        if (redisTemplate.hasKey(dishkey)) {
            log.info("查询缓存");
            return Result.success((List<DishVO>) redisTemplate.opsForValue().get(dishkey));
        }
        //封装dto条件
        DishPageDTO dishPageDTO = DishPageDTO.builder()
                .categoryId(categoryId)
                .status(1)
                .build();
        //调用service查询，返回结果
        log.info("查询数据库");
        voList = dishService.getParamList(dishPageDTO);
        //将数据库数据同步到缓存
        redisTemplate.opsForValue().set(dishkey,voList);

        return Result.success(voList);
    }
}
