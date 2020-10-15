package com.jannchie.biliob.constant;

import com.jannchie.biliob.exception.BusinessException;

/**
 * @author jannchie
 */
public enum ExceptionEnum {

    /**
     * Parameter out of ResponseEntity
     */
    ALREADY_SIGNED(-1, "已经签过到了"),
    OUT_OF_RANGE(-1, "参数超出范围"),
    NOT_LOGIN(-1, "未登录"),
    EXECUTE_FAILURE(-1, "执行失败"),
    CREDIT_NOT_ENOUGH(-1, "积分不足");

    private Integer code;
    private String msg;

    ExceptionEnum(Integer code, String msg) {
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

    public BusinessException getException() {
        return new BusinessException(this);
    }
}
