package com.sky.web.app;

import com.sky.dto.SetmealPageDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
public class AppSetmealController {
    @Autowired
    private SetmealService setmealService;
    /*根据分类id查询起售的套餐列表*/
    @GetMapping("/list")
    public Result getList(@RequestParam Long categoryId) {
        // 封装条件
        SetmealPageDTO setmealPageDTO = SetmealPageDTO.builder()
                .categoryId(categoryId)
                .status(1)
                .build();
        // 调用service查询
        List<SetmealVO> voList = setmealService.getParamList(setmealPageDTO);
        //返回
        return Result.success(voList);
    }
}
