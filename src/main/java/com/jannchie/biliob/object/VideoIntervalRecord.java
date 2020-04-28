package com.jannchie.biliob.object;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author Jannchie
 */
@Document(collection = "video_interval")
public class VideoIntervalRecord {
    private Long aid;
    private String bvid;
    private Date date;
    private Date next;
    private List<Object> order;
    private Integer interval;

    public VideoIntervalRecord() {
    }

    public VideoIntervalRecord(Long aid, Integer interval, Date time) {
        this.aid = aid;
        this.interval = interval;
        this.date = time;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public List<Object> getOrder() {
        return order;
    }

    public void setOrder(List<Object> order) {
        this.order = order;
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
