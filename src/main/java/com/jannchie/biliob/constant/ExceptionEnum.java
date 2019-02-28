package com.jannchie.biliob.constant;

/** @author jannchie */
public enum ExceptionEnum {

  /** Parameter out of range */
  OUT_OF_RANGE(-1, "参数超出范围");

  private Integer code;
  private String msg;

  ExceptionEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
