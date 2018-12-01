package com.jannchie.biliob.utils;

public enum ResultEnum {
  /** 成功执行 */
  SUCCEED(1, "成功"),
  /** 密码错误 */
  WRONG_PASSWORD(-1, "密码错误"),
  /** 用户不存在 */
  USER_NOT_EXIST(-1,"用户不存在"),
  /** 要查询的日期超出范围 */
  OUT_OF_RANGE(-1,"超出范围");
  public int code;
  public String msg;

  ResultEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
