package com.jannchie.biliob.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author Jannchie
 */
public class ChangePasswordForm extends BaseActivationCodeForm {
    @Email
    private String mail;

    @Length(min = 6, message = "密码至少为6位")
    @NotBlank(message = "用户密码不能为空!")
    private String password;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
