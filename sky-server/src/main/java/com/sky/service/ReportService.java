package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    /*营业额统计*/
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /*用户数据统计*/
    Object getUserStatistics(LocalDate begin, LocalDate end);

    /*订单统计*/
    Object getOrdersStatistics(LocalDate begin, LocalDate end);

    /*销量排名*/
    Object getTop10(LocalDate begin, LocalDate end);
}
