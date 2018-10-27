package com.jannchie.biliob.exception;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class BindExceptionHanlder {

    private static final Logger logger = LogManager.getLogger(BindExceptionHanlder.class);

    @ExceptionHandler(BindException.class)
    //将返回的值转成json格式的数据
    @ResponseBody
    //返回的状态码
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)     //400
    public ExceptionResult handleBindException(BindException ex) {
        // ex.getFieldError():随机返回一个对象属性的异常信息。如果要一次性返回所有对象属性异常信息，则调用ex.getAllErrors()
        FieldError fieldError = ex.getFieldError();
        // 生成返回结果
        ExceptionResult errorResult = new ExceptionResult();
        errorResult.setCode(400);
        errorResult.setMsg("数据验证失败");
        return errorResult;
    }
}
