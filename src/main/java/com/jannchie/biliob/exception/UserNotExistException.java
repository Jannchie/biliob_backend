package com.jannchie.biliob.exception;

/**
 * @author jannchie
 */
public class UserNotExistException extends RuntimeException {

	private String name;

	/**
	 * Constructs a new runtime exception with {@code null} as its detail message. The cause is not
	 * initialized, and may subsequently be initialized by a call to {@link #initCause}.
	 */
	public UserNotExistException(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
