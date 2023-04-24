package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /*根据用户查询数据库*/
    @Select("select * from employee where username=#{username}")
    Employee getBetbyUsername(String username);

    /*员工分页条件查询*/
    List<Employee> getList(String name);

    /*根据手机号查询*/
    @Select("select * from employee where phone=#{phone}")
    Employee getByPhone(String phone);

    /*根据身份证号查询*/
    @Select("select * from employee where id_number=#{idNumber}")
    Employee getIdNumber(String idNumber);

    /*新增员工*/
    void insert(Employee employee);

    /*回显员工，根据id查询*/
    @Select("select * from employee where id =#{id}")
    Employee getById(Long id);

    /*修改员工*/
    void updateById(Employee employee);
}
