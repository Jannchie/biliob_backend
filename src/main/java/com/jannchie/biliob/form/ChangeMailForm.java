package com.jannchie.biliob.form;

import javax.validation.constraints.Email;

/**
 * @author Jannchie
 */
public class ChangeMailForm {
    @Email
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

