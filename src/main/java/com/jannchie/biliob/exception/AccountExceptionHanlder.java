package com.jannchie.biliob.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AccountException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jannchie
 */
@RestControllerAdvice
public class AccountExceptionHanlder {

	private static final Logger logger = LogManager.getLogger(AccountExceptionHanlder.class);

	@ResponseBody
	@ExceptionHandler(AccountException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ExceptionResult handleAccountException(AccountException ex) {
		String msg = ex.getMessage();
		// 生成返回结果
		ExceptionResult errorResult = new ExceptionResult();
		errorResult.setCode(403);
		errorResult.setMsg(msg);
		return errorResult;
	}
}
