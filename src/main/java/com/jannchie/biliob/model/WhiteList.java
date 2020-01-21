package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "whitelist")
public class WhiteList {
    private String ip;
    private Date date;

    public WhiteList(String ip) {
        this.ip = ip;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
