package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class BangumiData {
    @Id
    private ObjectId id;
    private Long sid;
    private Long play;
    private Long danmaku;
    private Long coin;
    private Integer followOld;
    private Integer followNew;
    private Integer followSeries;
    private Integer undertake;
    private Float rate;
    private Integer rateCount;
    private Date datetime;

    public BangumiData() {
    }

    public BangumiData(Long sid, Long play, Long danmaku, Long coin, Integer followOld, Integer followNew, Integer followSeries, Integer undertake, Float rate, Integer rateCount, Date datetime) {
        this.sid = sid;
        this.play = play;
        this.danmaku = danmaku;
        this.coin = coin;
        this.followOld = followOld;
        this.followNew = followNew;
        this.followSeries = followSeries;
        this.undertake = undertake;
        this.rate = rate;
        this.rateCount = rateCount;
        this.datetime = datetime;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getPlay() {
        return play;
    }

    public void setPlay(Long play) {
        this.play = play;
    }

    public Long getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(Long danmaku) {
        this.danmaku = danmaku;
    }

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }

    public Integer getFollowOld() {
        return followOld;
    }

    public void setFollowOld(Integer followOld) {
        this.followOld = followOld;
    }

    public Integer getFollowNew() {
        return followNew;
    }

    public void setFollowNew(Integer followNew) {
        this.followNew = followNew;
    }

    public Integer getFollowSeries() {
        return followSeries;
    }

    public void setFollowSeries(Integer followSeries) {
        this.followSeries = followSeries;
    }

    public Integer getUndertake() {
        return undertake;
    }

    public void setUndertake(Integer undertake) {
        this.undertake = undertake;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Integer getRateCount() {
        return rateCount;
    }

    public void setRateCount(Integer rateCount) {
        this.rateCount = rateCount;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
