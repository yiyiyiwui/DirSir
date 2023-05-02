package com.sky.web.app;

import cn.hutool.core.util.ArrayUtil;
import com.sky.context.ThreadLocalUtil;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /*查询收货地址列表*/
    @GetMapping("/list")
    public Result getList() {
        // 获取登录人id
        Long userId = ThreadLocalUtil.getCurrentId();
        // 封装条件实体
        AddressBook addressBookParam = AddressBook.builder().userId(userId).build();
        // 调用service查询
        List<AddressBook> list = addressBookService.getList(addressBookParam);
        //返回结果
        return Result.success(list);
    }

    /*新增收货地址*/
    @PostMapping
    public Result save(@RequestBody AddressBook addressBook) {
        //调用service 新增
        addressBookService.save(addressBook);
        return Result.success();
    }

    /*查询默认地址*/
    @GetMapping("/default")
    public Result getdefault() {
        // 构建条件
        AddressBook addressBookParam = AddressBook.builder()
                .userId(ThreadLocalUtil.getCurrentId())
                .isDefault(1)
                .build();
        // 调用service条件查询
        List<AddressBook> list = addressBookService.getList(addressBookParam);
        //判断是否有默认地址
        if (ArrayUtil.isNotEmpty(list)) {
            AddressBook defaultAddress = list.get(0);
            return Result.success(defaultAddress);
        } else {
            return Result.success();
        }
    }

    /*回显地址*/
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /*修改地址*/
    @PutMapping
    public Result updateById(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    /*删除地址*/
    @DeleteMapping
    public Result deleteById(Long id) {
        addressBookService.deleteByid(id);
        return Result.success();
    }

    /*设置默认地址*/
    @PutMapping("/default")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
}
