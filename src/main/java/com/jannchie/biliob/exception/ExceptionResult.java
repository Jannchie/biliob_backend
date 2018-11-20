package com.jannchie.biliob.exception;

/**
 * @author jannchie
 */
public class ExceptionResult {

	private String msg;

	private Integer code;

	public String getMsg() {
		return msg;
	}

	void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	void setCode(Integer code) {
		this.code = code;
	}
}
