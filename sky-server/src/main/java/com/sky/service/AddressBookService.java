package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {


    /*查询收货地址列表*/
    List<AddressBook> getList(AddressBook addressBookParam);

    /*新增收获地址*/
    void save(AddressBook addressBook);

    /*回显地址*/
    AddressBook getById(long id);

    /*修改地址*/
    void updateById(AddressBook addressBook);

    /*删除地址*/
    void deleteByid(Long id);

    /*设置默认地址*/
    void setDefault(AddressBook addressBook);
}
