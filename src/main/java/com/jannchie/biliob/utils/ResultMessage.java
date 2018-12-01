package com.jannchie.biliob.utils;

public enum  ResultMessage {
  WRONG_PASSWORD(-1,"密码错误"),
  USER_NOT_EXIST(-1,"用户不存在");
  public int code;
  public String msg;

  ResultMessage(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
