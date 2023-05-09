package com.sky.web.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    private final WorkspaceService workspaceService;

    /*今日数据*/
    @GetMapping("/businessData")
    public Result getBusinessData() {
        //1获取当天时间
        LocalDate currentDate = LocalDate.now();
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(currentDate);
        return Result.success(businessDataVO);
    }

    /*订单管理*/
    @GetMapping("/overviewOrders")
    public Result getOverviewOrders() {
        LocalDate localDate = LocalDate.now();
        return Result.success(workspaceService.getOverviewOrders(localDate));
    }
}
