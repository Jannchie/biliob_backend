package com.jannchie.biliob.object;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author Jannchie
 */
public class LoginForm {
    @NotNull
    @Length(max = 20, min = 2)
    private String name;

    @NotNull
    @Length(max = 20, min = 6)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
