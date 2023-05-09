package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.User;
import com.sky.vo.BusinessDataVO;

import java.time.LocalDate;

public interface WorkspaceService  {

    /*今日数据*/
    BusinessDataVO getBusinessData(LocalDate currentDate);

    /*订单管理*/
    Object getOverviewOrders(LocalDate localDate);

}
