package com.jannchie.word.object;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author Jannchie
 */
public class LoginForm {
    @NotNull
    @Length(max = 12, min = 2)
    private String username;

    @NotNull
    @Length(max = 20, min = 6)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
