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
  HAS_NOT_LOGGED_IN(-1, "未登录"),
  WRONG_PASSWORD(-1, "密码错误"),
  USER_NOT_EXIST(-1,"用户不存在"),
  OUT_OF_RANGE(-1, "超出范围"),
  ALREADY_FAVORITE_AUTHOR(-1, "已经在关注了此作者"),
  ALREADY_FAVORITE_VIDEO(-1, "已经收藏了此视频"), ADD_FAVORITE_VIDEO_SUCCEED(1, "收藏成功"), ADD_FAVORITE_AUTHOR_SUCCEED(1, "关注成功"), DELETE_SUCCEED(1, "删除成功"), AUTHOR_NOT_FOUND(-1, "未找到该作者");

  public int code;
  public String msg;

  ResultEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
