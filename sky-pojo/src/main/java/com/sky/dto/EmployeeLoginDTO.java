package com.sky.dto;


import lombok.Data;

import java.io.Serializable;

@Data
//员工登录
public class EmployeeLoginDTO implements Serializable {

    private String username;

    private String password;

}
