package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.context.ThreadLocalUtil;
import com.sky.entity.AddressBook;
import com.sky.exception.BusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /*查询收获地址列表*/
    @Override
    public List<AddressBook> getList(AddressBook addressBookParam) {
        return addressBookMapper.getList(addressBookParam);
    }

    /*新增收货地址*/
    @Override
    public void save(AddressBook addressBook) {
        //1 参数校验
        if (StrUtil.isBlank(addressBook.getConsignee())
                || StrUtil.isBlank(addressBook.getSex())
                || addressBook.getPhone()==null
                || StrUtil.isBlank(addressBook.getDetail())) {
            throw new BusinessException("请补全信息");
        }
        //2 补全信息
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        addressBook.setIsDefault(0);//非默认
        //3 调用mapper新增
        addressBookMapper.insert(addressBook);
    }

    /*回显地址*/
    @Override
    public AddressBook getById(long id) {
        //直接根据id查询保存的地址即可
        return addressBookMapper.getById(id);
    }

    /*修改地址*/
    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
    }

    /*删除地址*/
    @Override
    public void deleteByid(Long id) {
        addressBookMapper.deleteById(id);
    }

    /*设置默认地址*/
    @Override
    public void setDefault(AddressBook addressBook) {
        //把当前用户的所有地址修改为非默认地址
        addressBook.setIsDefault(0);
        addressBook.setUserId(ThreadLocalUtil.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);
        addressBook.setIsDefault(1);
        addressBookMapper.updateById(addressBook);
    }
}
