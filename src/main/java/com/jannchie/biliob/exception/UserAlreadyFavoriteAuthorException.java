package com.jannchie.biliob.exception;

/** @author jannchie */
public class UserAlreadyFavoriteAuthorException extends Exception {

  private Long mid;

  public UserAlreadyFavoriteAuthorException(Long mid) {
    this.mid = mid;
  }

  public Long getMid() {
    return mid;
  }

  public void setMid(Long mid) {
    this.mid = mid;
  }
}
