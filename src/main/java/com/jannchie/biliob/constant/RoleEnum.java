package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */

public enum RoleEnum {
  /**
   * SIGN: Every eight hour, user can sign in once time.
   */
  NORMAL_USER(0, "普通用户");

  private Integer code;
  private String name;

  RoleEnum(Integer code) {
    this.code = code;
  }

  RoleEnum(Integer code, String name) {
    this.code = code;
    this.name = name;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
