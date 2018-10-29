package com.jannchie.biliob.utils;

public class Message {

    private Integer code;

    public Message(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {

        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;
}
