package com.sky.test;

import com.alibaba.druid.sql.visitor.functions.Lcase;
import com.sky.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

@SpringBootTest
@SuppressWarnings("all")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
        System.out.println(redisTemplate);
        //1 字符串类型
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //2 哈希类型
        HashOperations hashOperations = redisTemplate.opsForHash();
        //3 列表类型
        ListOperations listOperations = redisTemplate.opsForList();
        //4 集合类型
        SetOperations setOperations = redisTemplate.opsForSet();
        //5 有序集合类型
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
    }

    /*字符串类型*/
    @Test
    public void test1(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //存
        valueOperations.set("mystr","Heloo");
        //取
        System.out.println(valueOperations.get("mystr"));
        //存短信验证码，5分钟有效时间
        valueOperations.set("sms:13145670676","8888", Duration.ofSeconds(300));
        //存一个java对象 （要求实现序列化接口）
        Category category = Category.builder()
                .name("烤鱼套餐")
                .status(1)
                .createTime(LocalDateTime.now())
                .build();
        valueOperations.set("category",category);

        //查询java对象
        System.out.println(valueOperations.get("category"));
    }

    /*哈希类型*/
    @Test
    public void test2() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        //存
        hashOperations.put("user:1","name","路飞");
        hashOperations.put("user:1","age","18");
        //取一个
        System.out.println(hashOperations.get("user:1", "age"));
        //取出所有元素
        Set hkeys = hashOperations.keys("user:1");
        hkeys.forEach(hkey->{
            System.out.println(hashOperations.get("user:1", hkey));
        });
        //或
        System.out.println("------------------------------------");
        for (Object hkey : hkeys) {
            System.out.println(hashOperations.get("user:1", hkey));
        }
        //删除
        hashOperations.delete("user:1", "age");
    }

    /*列表类型*/
    @Test
    public void test3() {
        ListOperations listOperations = redisTemplate.opsForList();
        //左压入
        listOperations.leftPushAll("mylist", "a", "b", "c");
        //查询
        System.out.println(listOperations.range("mylist", 0, -1));
        //左弹出
        System.out.println(listOperations.leftPop("mylist"));
        //查询
        List mylist1 = listOperations.range("mylist", 0, -1);
        System.out.println(mylist1);
        //右弹出
        System.out.println(listOperations.rightPop("mylist"));
        //查询
        List mylist2 = listOperations.range("mylist", 0, -1);
        System.out.println(mylist2);
    }

    /*集合类型*/
    @Test
    public void test4() {
        SetOperations setOperations = redisTemplate.opsForSet();
        //存张三
        setOperations.add("zhangsan", "吃饭", "睡觉", "敲代码");
        //存李四
        setOperations.add("lisi", "饿着", "熬夜", "敲代码");
        //查询
        Set set = setOperations.members("lisi");
        System.out.println(set);
        //交集（共同点） [敲代码]
        System.out.println(setOperations.intersect("zhangsan", "lisi"));
        //并集(一起都有的，重复的会并一起） [饿着, 敲代码, 吃饭, 睡觉, 熬夜]
        System.out.println(setOperations.union("zhangsan", "lisi"));
    }

    /*有序集合类型*/
    @Test
    public void test5() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        //存
        zSetOperations.add("faker", "流浪法师", 66.6);
        zSetOperations.add("uzi", "暗夜猎手", 88.8);
        zSetOperations.add("小白", "德玛西亚之力", 0.66);
        //添加胜率
        System.out.println(zSetOperations.incrementScore("uzi", "暗夜猎手", 20));
        //查询
        System.out.println(zSetOperations.reverseRange("uzi", 0, -1));
    }

    /*通用操作*/
    @Test
    public void test6() {
        //查询 keys c*
        Set keys = redisTemplate.keys("c*");
        System.out.println(keys);
        //批量删除
        redisTemplate.delete(keys);
        //判断key是否存在
        System.out.println(redisTemplate.hasKey("uzi"));
    }
}
