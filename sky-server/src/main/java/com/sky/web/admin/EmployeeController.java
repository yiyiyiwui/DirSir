package com.sky.web.admin;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.ResultExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.time.Period;
import java.util.HashMap;

/**
 * 员工管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    //员工登录
    @PostMapping("login")
    public Result login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        //调用service登录
        Employee employee = employeeService.login(employeeLoginDTO);
        //jwt制作令牌~
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("empId", employee.getId());
        /*下面方法第一个参数是JWT的加密密匙，第二个参数是JWT的过期时间，第三个参数是把信息放在集合中*/
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecret(), jwtProperties.getAdminTtl(), claims);
        //返回vo
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .token(token)
                .userName(employee.getUsername()).build();
        return Result.success(employeeLoginVO);
    }

    /*员工退出*/
    @PostMapping("/logout")
    public Result logout() {
        return Result.success();
    }


    /*员工分页*/
    @GetMapping("/page")
    public Result getpage(EmployeePageQueryDTO employeePageQueryDTO) {
        //调用service
        PageResult pageResult = employeeService.getpage(employeePageQueryDTO);
        //返回分页结果
        return Result.success(pageResult);
    }

    /*新增员工*/
    @PostMapping //上面定义了一样的，所以这里可以省略不写
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        //调用service
        employeeService.save(employeeDTO);
        //返回success
        return Result.success();
    }

    /*回显员工*/
    @GetMapping("/{id}")
    public Result getByid(@PathVariable("id") Long id) {
        //调用service
        Employee employee = employeeService.getById(id);
        //返回
        return Result.success(employee);
    }

    /*修改员工*/
    @PutMapping
    public Result updateById(@RequestBody EmployeeDTO employeeDTO) {
        //调用service
        employeeService.updateById(employeeDTO);
        //返回success
        return Result.success();
    }

    /*禁用员工*/
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, @RequestParam Long id) {
        //调用service
        employeeService.startOrStop(status, id);
        //返回success
        return Result.success();
    }
}
