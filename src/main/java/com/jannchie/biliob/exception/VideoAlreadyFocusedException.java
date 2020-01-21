package com.jannchie.biliob.exception;

/**
 * @author jannchie
 */
public class VideoAlreadyFocusedException extends Exception {

    private Long aid;

    /**
     * Constructs a new exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public VideoAlreadyFocusedException(Long aid) {
        this.aid = aid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }
}
