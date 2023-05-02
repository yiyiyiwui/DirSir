package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /*查询收货地址列表*/
    List<AddressBook> getList(AddressBook addressBookParam);

    /*新增收货地址*/
    void insert(AddressBook addressBook);

    /*回显地址*/
    @Select("select * from address_book where id=#{id}")
    AddressBook getById(long id);


    /*修改地址*/
    void updateById(AddressBook addressBook);

    /*删除地址*/
    @Delete("delete * from address_book id= #{id}")
    void deleteById(Long id);

    /*设置默认地址  根据用户id修改默认地址*/
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);
}
