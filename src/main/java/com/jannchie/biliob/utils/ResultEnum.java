package com.jannchie.biliob.utils;

/**
 * @author jannchie
 */

public enum ResultEnum {
  /**
   * Enumerated all possible information returned,
   * where the first item is a status code,
   * the second item is a short description.
   */
  SUCCEED(1, "成功"),
  LOGIN_SUCCEED(1, "登录成功"),
  LOGIN_FAILED(-1, "登录失败"),
  WRONG_PASSWORD(-1, "密码错误"),
  USER_NOT_EXIST(-1,"用户不存在"),
  OUT_OF_RANGE(-1,"超出范围");
  public int code;
  public String msg;

  ResultEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
