package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /*根据openid查询*/
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    /*注册*/
    void insert(User user);


    /*统计当天注册用户*/
    @Select("select count(*) from user where date(create_time) = #{currentDate}")
    Integer countUser(LocalDate currentDate);




}
