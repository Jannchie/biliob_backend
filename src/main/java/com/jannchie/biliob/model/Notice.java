package com.jannchie.biliob.model;

import java.util.Date;

/**
 * @author Jannchie
 */
public class Notice {
    private Date date;
    private String msg;
    private Integer type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
