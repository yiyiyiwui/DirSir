package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
//用户登录
public class UserLoginDTO implements Serializable {

    private String code;

}
