package com.jannchie.biliob.object;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Jannchie
 */
public class NickNameForm {
    @NotNull
    @NotBlank
    @Length(max = 50, min = 2, message = "昵称最长为50位，最短为2位")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
