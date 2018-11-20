package com.jannchie.biliob.exception;

/**
 * @author jannchie
 */
public class AuthorAlreadyFocusedException extends Exception {

	private Long mid;

	/**
	 * Constructs a new exception with {@code null} as its detail message. The cause is not
	 * initialized, and may subsequently be initialized by a call to {@link #initCause}.
	 */
	public AuthorAlreadyFocusedException(Long mid) {
		this.mid = mid;
	}

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}
}
