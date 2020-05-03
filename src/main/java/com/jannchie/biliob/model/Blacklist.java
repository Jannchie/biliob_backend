package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "blacklist")
public class Blacklist {
    private String ip;
    private Date date;
    private String reason;

    public Blacklist(String ip) {
        this.ip = ip;
        this.date = new Date();
    }

    public Blacklist(String ip, String reason) {
        this.ip = ip;
        this.date = new Date();
        this.reason = reason;
    }

    public Blacklist(String ip, String reason, Boolean forever) {
        this.ip = ip;
        this.reason = reason;
        if (!forever) {
            this.date = new Date();
        }
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
