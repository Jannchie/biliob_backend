package com.jannchie.biliob.exception;

public class AuthorAlreadyFocusedException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public AuthorAlreadyFocusedException(Long mid) {
        this.mid = mid;
    }

    private Long mid;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }
}
