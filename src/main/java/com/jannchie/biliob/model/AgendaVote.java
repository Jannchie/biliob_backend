package com.jannchie.biliob.model;

import com.jannchie.biliob.constant.UserOpinion;

/**
 * @author Jannchie
 */
public class AgendaVote {
    User user;
    UserOpinion opinion;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserOpinion getOpinion() {
        return opinion;
    }

    public void setOpinion(UserOpinion opinion) {
        this.opinion = opinion;
    }
}
