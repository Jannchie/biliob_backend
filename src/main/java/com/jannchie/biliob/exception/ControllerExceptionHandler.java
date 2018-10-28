package com.jannchie.biliob.exception;

import com.jannchie.biliob.service.serviceImpl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LogManager.getLogger(ControllerExceptionHandler.class);

    /**
     * 处理用户名已存在的异常
     *
     * @param userAlreadyExistException 用户已存在异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)     //服务内部错误
    public ExceptionResult handlerUserAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("用户名已经存在");
        // 记录下重名信息
        logger.info(userAlreadyExistException.getName());
        return ex;
    }

    /**
     * 处理用户名已存在的异常
     *
     * @param userNotExistException 用户已存在异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)     //服务内部错误
    public ExceptionResult handlerUserNotExistException(UserNotExistException userNotExistException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("用户不存在");
        // 记录下不存在的用户信息
        logger.info(userNotExistException.getName());
        return ex;
    }
}