package com.jannchie.biliob.exception;

/** @author jannchie */
public class UserAlreadyFavoriteVideoException extends Exception {

  private Long aid;

  public UserAlreadyFavoriteVideoException(Long aid) {
    this.aid = aid;
  }

  public Long getAid() {
    return aid;
  }

  public void setAid(Long aid) {
    this.aid = aid;
  }
}
