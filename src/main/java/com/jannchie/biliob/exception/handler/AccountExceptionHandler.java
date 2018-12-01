package com.jannchie.biliob.exception.handler;

import com.jannchie.biliob.utils.ExceptionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jannchie
 */
@RestControllerAdvice
public class AccountExceptionHandler {

	private static final Logger logger = LogManager.getLogger(AccountExceptionHandler.class);

	@ResponseBody
  @ExceptionHandler(IncorrectCredentialsException.class)
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public ExceptionResult handleAccountException() {
    // 生成返回结果
    ExceptionResult errorResult = new ExceptionResult();
    errorResult.setCode(403);
    errorResult.setMsg("密码错误");
    logger.info("密码错误");
    return errorResult;
  }

  @ResponseBody
  @ExceptionHandler(ShiroException.class)
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public ExceptionResult handleShiroException() {
    // 生成返回结果
    ExceptionResult errorResult = new ExceptionResult();
    errorResult.setCode(403);
    errorResult.setMsg("登录失败");
    logger.info("登录失败");
    return errorResult;
  }
}
