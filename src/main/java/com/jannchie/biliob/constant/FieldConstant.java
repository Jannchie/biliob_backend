package com.jannchie.biliob.constant;

/** @author jannchie */
public enum FieldConstant {
  /** CREDIT: Credit of user. */
  CREDIT("credit");

  private String value;

  FieldConstant(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
