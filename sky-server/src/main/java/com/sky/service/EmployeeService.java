package com.sky.service;

import com.sky.result.PageResult;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

@Transactional//事务
public interface EmployeeService {
    /**员工登录*/
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /*员工分页*/
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /*新增员工*/
    void save(EmployeeDTO employeeDTO);


    /*回显员工*/
    Employee getById(Long id);

    /*修改员工*/
    void updateById(EmployeeDTO employeeDTO);

    /*禁用员工*/
    void startOrStop(Integer status, Long id);

}
