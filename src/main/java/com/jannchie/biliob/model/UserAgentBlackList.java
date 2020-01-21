package com.jannchie.biliob.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Document(collection = "user_agent_black_list")
public class UserAgentBlackList {
    private String userAgent;
    private Date date;

    public UserAgentBlackList(String userAgent) {
        this.userAgent = userAgent;
        this.date = Calendar.getInstance().getTime();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
