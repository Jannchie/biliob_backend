package com.jannchie.biliob.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
  private Integer code;

  public Integer getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public Object getData() {
    return data;
  }

  private String msg;
  private Object data;

  public Result(ResultEnum resultEnum) {
    this.code = resultEnum.code;
    this.msg = resultEnum.msg;
  }

  public Result(ResultEnum resultEnum, Object data) {
    this.code = resultEnum.code;
    this.msg = resultEnum.msg;
    this.data = data;
  }
}

