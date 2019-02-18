package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum CreditConstant {
  /**
   * SIGN: Every eight hour, user can sign in once time.
   */
  CHECK_IN(10, "签到"),
  SET_FORCE_OBSERVE(-100, "设置强制追踪"),
  ASK_QUESTION(-30, "提出问题"),
  REFRESH_AUTHOR_DATA(-10, "立即刷新作者数据"),
  REFRESH_VIDEO_DATA(-5, "立即刷新视频数据");

  private Integer value;
  private String msg;

  CreditConstant(Integer value, String msg) {
    this.value = value;
    this.msg = msg;
  }

  CreditConstant(Integer value) {
    this.value = value;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }
}
