package com.jannchie.biliob.exception;

import javax.validation.Valid;

public class UserAlreadyFavoriteVideoException extends Throwable {
    private Long aid;

    public UserAlreadyFavoriteVideoException(@Valid Long aid) {
        this.aid = aid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long mid) {
        this.aid = aid;
    }
}
