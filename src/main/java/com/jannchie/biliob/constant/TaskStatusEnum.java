package com.jannchie.biliob.constant;

/** @author jannchie */
public enum TaskStatusEnum {
  /** status of task */
  START(1),
  UPDATE(2),
  DEAD(4),
  ALIVE(5),
  WARNING(6),
  TIMEOUT(8),
  FINISHED(9);

  public final Integer value;

  TaskStatusEnum(Integer value) {
    this.value = value;
  }
}
