package com.jannchie.biliob.exception;

import com.jannchie.biliob.utils.ExceptionMessage;

/**
 * @author jannchie
 */
public class BusinessException extends RuntimeException {

  private final ExceptionMessage exceptionMessage;

  /**
   * Constructs a new runtime exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public BusinessException(ExceptionMessage exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  public ExceptionMessage getExceptionMessage() {
    return exceptionMessage;
  }
}
