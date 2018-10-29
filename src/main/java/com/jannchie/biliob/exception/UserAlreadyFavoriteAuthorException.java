package com.jannchie.biliob.exception;

import javax.validation.Valid;

public class UserAlreadyFavoriteAuthorException extends Throwable {
    private Long mid;
    public UserAlreadyFavoriteAuthorException(@Valid Long mid) {
        this.mid = mid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }
}
