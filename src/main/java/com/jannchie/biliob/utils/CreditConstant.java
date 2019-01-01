package com.jannchie.biliob.utils;

/**
 * @author jannchie
 */

public enum CreditConstant {
  /**
   * SIGN: Every eight hour, user can sign in once time.
   */
  SIGN(10);

  public Integer value;

  CreditConstant(Integer value) {
    this.value = value;
  }
}
