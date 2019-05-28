package com.jannchie.biliob.constant;

/** @author jannchie */
public enum ResultEnum {
  /**
   * Enumerated all possible information returned, where the first item is a status code, the second
   * item is a short description.
   */
  SUCCEED(1, "成功"),
  LOGIN_SUCCEED(1, "登录成功"),
  LOGIN_FAILED(-1, "登录失败"),
  HAS_NOT_LOGGED_IN(-1, "未登录"),
  WRONG_PASSWORD(-1, "密码错误"),
  USER_NOT_EXIST(-1, "用户不存在"),
  OUT_OF_RANGE(-1, "超出范围"),
  PARAM_ERROR(-1, "参数错误"),
  ALREADY_FAVORITE_AUTHOR(-1, "已经在关注了此作者"),
  ALREADY_FAVORITE_VIDEO(-1, "已经收藏了此视频"),
  ADD_FAVORITE_VIDEO_SUCCEED(1, "收藏成功"),
  ADD_FAVORITE_AUTHOR_SUCCEED(1, "关注成功"),
  DELETE_SUCCEED(1, "删除成功"),
  AUTHOR_NOT_FOUND(-1, "未找到该作者"),
  ALREADY_SIGNED(-1, "已经签过到了"),
  SIGN_SUCCEED(1, "签到成功"),
  PERMISSION_DENIED(-1, "权限不足"),
  CREDIT_NOT_ENOUGH(-1, "积分不足"),
  ALREADY_FORCE_FOCUS(-1, "已经强制观测了该作者"),
  USER_ALREADY_EXIST(-1, "用户名已被占用"),
  EXECUTE_FAILURE(-1, "执行失败"),
  AUTHOR_ALREADY_SUPPORTED(-1, "用户的应援会已建立过"),
  ACTIVATION_CODE_UNMATCHED(-1, "验证激活码错误"),
  MAIL_HAD_BEEN_REGISTERED(-1, "邮箱已被注册"),
  SEND_MAIL_FAILED(-1, "发送邮件失败,可能是目标邮箱不受支持");

  private int code;
  private String msg;

  ResultEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
