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
  public Result(ResultEnum exceptionEnum) {
    this.code =exceptionEnum.code;
    this.msg = exceptionEnum.msg;
  }

  public Result(ResultEnum resultType, Object data) {
    this.code = resultType.code;
    this.msg = resultType.msg;
    this.data = data;
  }
}

