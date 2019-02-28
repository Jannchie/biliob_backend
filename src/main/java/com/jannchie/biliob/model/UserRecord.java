package com.jannchie.biliob.model;

import java.util.Date;

/** @author jannchie */
public class UserRecord {

  private Date recordDatetime;
  private String message;
  private Integer credit;

  public UserRecord(Date recordDatetime, String message, Integer credit) {
    this.recordDatetime = recordDatetime;
    this.message = message;
    this.credit = credit;
  }

  public Date getRecordDatetime() {
    return recordDatetime;
  }

  public void setRecordDatetime(Date recordDatetime) {
    this.recordDatetime = recordDatetime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getCredit() {
    return credit;
  }

  public void setCredit(Integer credit) {
    this.credit = credit;
  }
}
