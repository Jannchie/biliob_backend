package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.ExceptionEnum;

/** @author jannchie */
public class ExceptionResult {

  private String msg;

  private Integer code;

  public ExceptionResult(ExceptionEnum exceptionEnum) {
    this.code = exceptionEnum.getCode();
    this.msg = exceptionEnum.getMsg();
  }

  public ExceptionResult() {}

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
