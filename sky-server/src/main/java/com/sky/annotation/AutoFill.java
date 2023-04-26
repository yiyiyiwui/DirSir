package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//注解作用范围
@Target({ElementType.METHOD})//注解可以标注的位置
public @interface AutoFill {
    //value属性，用户区分是insert 还是update
    String value() default "insert";
}
