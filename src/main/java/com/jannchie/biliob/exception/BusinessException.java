package com.jannchie.biliob.exception;

import com.jannchie.biliob.constant.ExceptionEnum;

/**
 * @author jannchie
 */
public class BusinessException extends RuntimeException {

    private final ExceptionEnum exceptionEnum;

    /**
     * Constructs a new runtime exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public BusinessException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }
}
