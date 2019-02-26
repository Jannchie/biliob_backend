package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum CreditConstant {
  /**
   * CHECK_IN: Every eight hour, user can sign in once time.
   * WATCH_AD: Every eight hour, user can watch ad to earn credit once time.
   * MODIFY_NAME: User modify his username.
   * SET_FORCE_OBSERVE: User set one video or author permanently be observed.
   * SET_STRONG_OBSERVE: User set one video or author be observed strongly and the frequency is once an hour.
   * SET_KICHIKU_OBSERVE: User set one video or author be observed kichiku and the frequency is once a minutes.
   * ASK_QUESTION: User are able to ask question and I will give my answer.
   * DONATE: If user try to donate money to me, he will earn credit.
   * REFRESH_AUTHOR_DATA: Refresh author data immediately.
   * REFRESH_AUTHOR_DATA: Refresh video data immediately.
   */
  CHECK_IN(10, "签到"),
  WATCH_AD(10, "点击广告"),
  MODIFY_NAME(-50, "改名"),
  SET_FORCE_OBSERVE(-200, "设置强制追踪"),
  SET_STRONG_OBSERVE(-150, "设置高频率追踪一周"),
  SET_KICHIKU_OBSERVE(-300, "设置鬼畜级频率追踪一周"),
  ASK_QUESTION(-30, "提出问题"),
  DONATE(100, "试图捐款"),
  REFRESH_AUTHOR_DATA(-5, "立即刷新作者数据"),
  REFRESH_VIDEO_DATA(-1, "立即刷新视频数据"),
  DANMAKU_AGGREGATE(-10, "进行弹幕分析");

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
