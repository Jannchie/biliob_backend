package com.jannchie.biliob.object;

import java.util.Date;

/**
 * @author Jannchie
 */
public class AuthorIntervalRecord {
    private Long mid;
    private Date date;
    private Date next;
    private Integer interval;

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
