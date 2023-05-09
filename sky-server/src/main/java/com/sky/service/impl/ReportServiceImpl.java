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
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.ValueOperations;
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
    private final UserMapper userMapper;

    /*营业额统计*/
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //1 调用方法
        List<LocalDate> dateList = getLocalDates(begin, end);
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

    /*拼接时间区间方法*/
    @NotNull
    private List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        //1 拼接时间区间（集合）
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
            // +1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /*用户数据统计*/
    @Override
    public Object getUserStatistics(LocalDate begin, LocalDate end) {
//        //1 拼接时间区间
//        List<LocalDate> dateList = new ArrayList<>();
//        dateList.add(begin);
//        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
//            // +1天
//            begin = begin.plusDays(1);
//            dateList.add(begin);
//        }
//        //2 新增用户数列表
//        List<Integer> newUserList = new ArrayList();// 新增用户数
//        List<Integer> totalUserList = new ArrayList();// 总用户数
//        for (LocalDate date : dateList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            //新增用户数量
//            Integer newUser = orderMapper.getUserCount(beginTime, endTime);
//            //总用户数量
//            Integer totalUser = orderMapper.getUserCount(null, endTime);
//            //把数量存到集合中
//            newUserList.add(newUser);
//            totalUserList.add(totalUser);
//        }
//
//        return UserReportVO.builder().dateList(StringUtils.join(dateList, ","))
//                .newUserList(StringUtils.join(newUserList, ","))
//                .totalUserList(StringUtils.join(totalUserList, ","))
//                .build();

        //1 填充日期集合
        List<LocalDate> localDates = getLocalDates(begin, end);
        //2 声明新增用户集合，总用户集合
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        //3 遍历日期集合
        Integer totalUser = 0;
        for (LocalDate localDate : localDates) {
            //3.1 查询每一天用户注册量
            Integer currentCount = userMapper.countUser(localDate);
            //3.2 累加总用户量
            totalUser += currentCount;
            totalUserList.add(totalUser);
            //3.3 添加新增用户量
            newUserList.add(currentCount);
        }

        //4 封装并返回VO
        return UserReportVO.builder().dateList(StringUtils.join(localDates, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }


    /*订单统计*/
    @Override
    public Object getOrdersStatistics(LocalDate begin, LocalDate end) {
//        //1 拼接时间区间
//        List<LocalDate> dateList = new ArrayList<>();
//        dateList.add(begin);
//        while (!begin.equals(end)) { // 从开始时间循环到结束时间，从5月1日到5-30日
//            // +1天
//            begin = begin.plusDays(1);
//            dateList.add(begin);
//        }
//        // 每天订单总数集合
//        List<Integer> orderCountList = new ArrayList<>();
//        //每天有效订单数集合
//        ArrayList<Integer> validOrderCountList = new ArrayList<>();
//        for (LocalDate date : dateList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            //查询每天的总订单数
//            Integer orderCount = orderMapper.getOrderCount(beginTime, endTime, null);
//            //查询每天的有效订单数
//            Integer validOrderCount = orderMapper.getOrderCount(beginTime, endTime, Orders.COMPLETED);
//            orderCountList.add(orderCount);
//            validOrderCountList.add(validOrderCount);
//        }
//
//        //时间区间内的总订单数
//        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
//        //时间区间内的总有效订单数
//        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
//        //订单完成率
//        Double orderCompletionRate = 0.0;
//        if (totalOrderCount != 0) {
//            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
//        }
//        return OrderReportVO.builder().dateList(StringUtils.join(orderCountList, ","))
//                .orderCountList(StringUtils.join(orderCountList, ","))
//                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
//                .totalOrderCount(totalOrderCount)
//                .validOrderCount(validOrderCount)
//                .orderCompletionRate(orderCompletionRate)
//                .build();
        //1 填充日期集合
        List<LocalDate> localDates = getLocalDates(begin, end);
        //2 声明
        //2.1 订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //2.2 有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();
        //2.3 订单总数
        Integer totalOrderCount = 0;
        //2.4 有效订单总数
        Integer validOrderCount = 0;
        //3 遍历日期
        for (LocalDate currentDate : localDates) {
            // 查询当天有效订单，总订单
            Integer countOrder = orderMapper.countOrder(null, currentDate);
            // 查询当天有效订单
            Integer countValidorder = orderMapper.countOrder(Orders.COMPLETED, currentDate);
            // 累加总订单，有效订单
            totalOrderCount = totalOrderCount + countOrder;
            // 累加有效订单
            validOrderCount = validOrderCount + countValidorder;
            // 添加当天总订单
            orderCountList.add(countOrder);
            // 添加当天有效订单
            validOrderCountList.add(validOrderCount);
        }
        //4 计算订单完成率
        Double orderCompletionRate = 0.0;
        if (validOrderCount > 0 && totalOrderCount > 0) {
            orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;
        }
        //5 封装并返回vo
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
        /*第一种方式*/
//        List<GoodsSalesDTO> goodsSalesDTOSList = orderMapper.getSalesTop10(beginTime, endTime);
//        String nameList = StringUtils.join(goodsSalesDTOSList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
//        String numberList = StringUtils.join(goodsSalesDTOSList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");
//
//        return SalesTop10ReportVO.builder()
//                .nameList(nameList)
//                .numberList(numberList)
//                .build();

        /*第二种*/
        // 1.直接调用mapper查询
//       List<GoodsSalesDTO> list = orderMapper.getSalesTop10(beginTime, endTime);
//        // 2.声明商品列表、销量列表
//        List<String> nameList = new ArrayList<>();
//        List<Integer> numberList = new ArrayList<>();
//        // 3.遍历结果集
//        for (GoodsSalesDTO goodsSalesDTO : list) {
//        // 3-1 添加名称
//            nameList.add(goodsSalesDTO.getName());
//        // 3-2 添加销量
//            numberList.add(goodsSalesDTO.getNumber());
//        }
//        // 4.封装并返回VO
//        return SalesTop10ReportVO.builder()
//                .nameList(StrUtil.join(",",nameList))
//                .numberList(StrUtil.join(",",numberList))
//                .build();

        /*第三种*/
        List<GoodsSalesDTO> goodsSalesDTOSList = orderMapper.getSalesTop10(beginTime, endTime);
        String baneList = StrUtil.join(",",goodsSalesDTOSList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()));
        String numberList = StrUtil.join( ",",goodsSalesDTOSList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()));
        return SalesTop10ReportVO.builder()
                .nameList(baneList)
                .numberList(numberList)
                .build();

        /**总结：StringUtils.join() 和 StrUtil.join() 方法的作用都是连接一个字符串数组或集合，并以指定的分隔符来将它们拼接成一个字符串。
              后者是hutool提供的，一个拼接字符串的逗号在前，一个在后
         */
    }

}
