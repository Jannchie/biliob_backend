package com.jannchie.biliob.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@Document("video_stat")
public class VideoStat {
    private Long aid;
    private String bvid;
    private Long coin;
    private Long danmaku;
    private Date dateTime;
    private Long favorite;
    private Long like;
    private Long jannchie;
    private Long reply;
    private Long share;
    private Long view;

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

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }

    public Long getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(Long danmaku) {
        this.danmaku = danmaku;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Long getFavorite() {
        return favorite;
    }

    public void setFavorite(Long favorite) {
        this.favorite = favorite;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }

    public Long getJannchie() {
        return jannchie;
    }

    public void setJannchie(Long jannchie) {
        this.jannchie = jannchie;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Long getShare() {
        return share;
    }

    public void setShare(Long share) {
        this.share = share;
    }

    public Long getView() {
        return view;
    }

    public void setView(Long view) {
        this.view = view;
    }
}
