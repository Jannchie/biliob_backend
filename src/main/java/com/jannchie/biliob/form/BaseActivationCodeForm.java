package com.jannchie.biliob.form;

import javax.validation.constraints.NotBlank;

/**
 * @author Jannchie
 */
public abstract class BaseActivationCodeForm {
    @NotBlank(message = "验证码不能为空!")
    private String activationCode;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}
