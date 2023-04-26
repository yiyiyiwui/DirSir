package com.sky.except;

import com.sky.exception.BusinessException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*全局异常处理类*/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /*预期异常*/
    @ExceptionHandler(BusinessException.class)
    public Result handlerBusinessException(BusinessException e) {
        log.error("出现业务异常{}", e);
        return Result.error(e.getCode(), e.getMessage());
    }
    /*非预期异常 Exception 兜底异常处理*/
    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e) {
        log.error("出现未知异常：{}",e);
        return Result.error(500, "未知异常，请稍后重试");
    }
}
