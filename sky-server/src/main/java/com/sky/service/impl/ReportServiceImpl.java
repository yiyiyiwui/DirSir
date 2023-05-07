package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.springframework.stereotype.Service;

import java.lang.reflect.GenericDeclaration;
import java.rmi.StubNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;

    /*营业额统计*/
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //1 拼接时间区间（集合）
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
            // +1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //2 声明 营业额集合
        List<Double> turnoverList = new ArrayList<>();
        //3 遍历时间
        for (LocalDate localDate : dateList) {
            //3.1 查询当天营业额，通过时间和状态去查每一天的营业额
            Double turnover = orderMapper.sumTurnover(Orders.COMPLETED, localDate); //已完成订单 和营业额
            //3.2 添加营业额
            if (turnover == null) {
                turnover = 0D;
            }
            // 把所有查到的营业额添加到集合中
            turnoverList.add(turnover);
        }
        //4 前端要两个值，需要封装起来一起返回
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StrUtil.join(",", dateList)) // 日期列表
                .turnoverList(StrUtil.join(",", turnoverList)) // 营业额列表
                .build();
        return turnoverReportVO;
    }

    /*用户数据统计*/
    @Override
    public Object getUserStatistics(LocalDate begin, LocalDate end) {
        //1 拼接时间区间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
            // +1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //2 新增用户数列表
        List<Integer> newUserList = new ArrayList();// 新增用户数
        List<Integer> totalUserList = new ArrayList();// 总用户数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //新增用户数量
            Integer newUser = orderMapper.getUserCount(beginTime, endTime);
            //总用户数量
            Integer totalUser = orderMapper.getUserCount(null, endTime);
            //把数量存到集合中
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder().dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }


    /*订单统计*/
    @Override
    public Object getOrdersStatistics(LocalDate begin, LocalDate end) {
        //1 拼接时间区间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
            // +1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数集合
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询每天的总订单数
            Integer orderCount = orderMapper.getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数
            Integer validOrderCount = orderMapper.getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder().dateList(StringUtils.join(orderCountList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /*销量排名*/
    @Override
    public Object getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOSList = orderMapper.getSalesTop10(beginTime, endTime);
        String nameList = StringUtils.join(goodsSalesDTOSList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(goodsSalesDTOSList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

}
