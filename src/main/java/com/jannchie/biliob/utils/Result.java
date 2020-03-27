package com.jannchie.biliob.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.constant.ResultEnum;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private User user;

    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public Result(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = data;
    }

    public Result(ResultEnum resultEnum, double credit, double exp) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.setUser(new User(credit, exp));
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    class User {
        private double credit;
        private double exp;

        public User() {
        }

        public User(double credit, double exp) {
            this.credit = credit;
            this.exp = exp;
        }

        public double getCredit() {
            return credit;
        }

        public void setCredit(double credit) {
            this.credit = credit;
        }

        public double getExp() {
            return exp;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }
    }
}
