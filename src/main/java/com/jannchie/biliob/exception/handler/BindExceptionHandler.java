package com.jannchie.biliob.exception.handler;

import com.jannchie.biliob.utils.ExceptionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jannchie
 */
@RestControllerAdvice
public class BindExceptionHandler {

    private static final Logger logger = LogManager.getLogger(BindExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handleBindException() {
        // 生成返回结果
        ExceptionResult errorResult = new ExceptionResult();
        errorResult.setCode(400);
        errorResult.setMsg("数据验证失败");
        logger.info("数据验证失败");
        return errorResult;
    }
}
