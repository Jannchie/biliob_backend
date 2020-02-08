package com.jannchie.biliob.object;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@Document(collection = "author_interval")
public class AuthorIntervalRecord {
    private Long mid;
    private Date date;
    private Date next;
    private Integer interval;

    AuthorIntervalRecord() {
    }

    public AuthorIntervalRecord(Long mid, Integer interval, Date time) {
        this.mid = mid;
        this.interval = interval;
        this.date = time;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getNext() {
        return next;
    }

    public void setNext(Date next) {
        this.next = next;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
