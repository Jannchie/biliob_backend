package com.jannchie.biliob.constant;

/** @author jannchie */
public enum TaskTypeEnum {

  /** GET_ALL: get all of the task; GET_RUNNING: only get the running task */
  GET_ALL(0),
  GET_RUNNING(1);

  public final Integer value;

  TaskTypeEnum(Integer value) {
    this.value = value;
  }
}
