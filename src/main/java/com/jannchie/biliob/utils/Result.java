package com.jannchie.biliob.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;
    private UserData user;


    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public Result(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = data;
    }

    public Result(ResultEnum resultEnum, User user) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.setUser(new UserData(user.getCredit(), user.getExp()));
    }

    public Result(ResultEnum resultEnum, double credit, double exp) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.setUser(new UserData(credit, exp));
    }

    public Result(ResultEnum resultEnum, T data, User user) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.user = new UserData(user.getCredit(), user.getExp());
        this.data = data;
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

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public static class UserData {
        private Double credit;
        private Double exp;

        public UserData() {
        }

        public UserData(double credit, double exp) {
            this.credit = credit;
            this.exp = exp;
        }

        public Double getCredit() {
            return credit;
        }

        public void setCredit(double credit) {
            this.credit = credit;
        }

        public Double getExp() {
            return exp;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }
    }
}
