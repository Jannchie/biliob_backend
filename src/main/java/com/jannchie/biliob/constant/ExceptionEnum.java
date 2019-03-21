package com.jannchie.biliob.constant;

/** @author jannchie */
public enum ExceptionEnum {

  /** Parameter out of range */
  ALREADY_SIGNED(-1, "已经签过到了"),
  OUT_OF_RANGE(-1, "参数超出范围"),
  EXECUTE_FAILURE(-1, "执行失败");

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
