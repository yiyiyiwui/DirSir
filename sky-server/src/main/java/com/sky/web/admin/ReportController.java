package com.sky.web.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.PushBuilder;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /*营业额统计*/
    @GetMapping("/turnoverStatistics")
    public Result getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        //调用service查询
        return Result.success(reportService.getTurnoverStatistics(begin, end));

    }

    /*用户数据统计*/
    @GetMapping("/userStatistics")
    public Result getUserStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /*订单统计*/
    @GetMapping("/ordersStatistics")
    public Result getOrdersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    /*销量排名*/
    @GetMapping("/top10")
    public Result getTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(reportService.getTop10(begin,end));
    }
}
