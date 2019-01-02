package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */

public enum CreditConstant {
  /**
   * SIGN: Every eight hour, user can sign in once time.
   */
  CHECK_IN(10), SET_FORCE_OBSERVE(-100);

  private Integer value;

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  CreditConstant(Integer value) {
    this.value = value;
  }

}
