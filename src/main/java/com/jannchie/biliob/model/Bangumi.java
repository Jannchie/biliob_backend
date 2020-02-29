package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "bangumi")
public class Bangumi {
    private Long sid;
    private Long mid;
    private String name;
    private String foreignName;
    private String copyright;
    // 专题类型/专题分区，其中1:番剧，4:国创
    private Short type;
    // 专题状态（-1: 下架, 2:免费, 13:大会员抢先/专享）
    private Byte state;
    private Date pubDate;
    private Boolean isSerializing;
    private Boolean isFinished;
    private Long cView;
    private Long cCoin;
    private Long oldFollow;
    private Long newFollow;
    private Float score;
    private Long scoreCount;
    private String cover;
    private String smallCover;
    private String charge;
    private String area;

    public Bangumi(Long sid, Long mid, String name, String foreignName, String copyright, Short type, Byte state, Date pubDate, Boolean isSerializing, Boolean isFinished, Long cView, Long cCoin, Long oldFollow, Long newFollow, Float score, Long scoreCount, String cover, String smallCover, String charge, String area) {
        this.sid = sid;
        this.mid = mid;
        this.name = name;
        this.foreignName = foreignName;
        this.copyright = copyright;
        this.type = type;
        this.state = state;
        this.pubDate = pubDate;
        this.isSerializing = isSerializing;
        this.isFinished = isFinished;
        this.cView = cView;
        this.cCoin = cCoin;
        this.oldFollow = oldFollow;
        this.newFollow = newFollow;
        this.score = score;
        this.scoreCount = scoreCount;
        this.cover = cover;
        this.smallCover = smallCover;
        this.charge = charge;
        this.area = area;
    }

    public Bangumi() {
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForeignName() {
        return foreignName;
    }

    public void setForeignName(String foreignName) {
        this.foreignName = foreignName;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Boolean getSerializing() {
        return isSerializing;
    }

    public void setSerializing(Boolean serializing) {
        isSerializing = serializing;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public Long getcView() {
        return cView;
    }

    public void setcView(Long cView) {
        this.cView = cView;
    }

    public Long getcCoin() {
        return cCoin;
    }

    public void setcCoin(Long cCoin) {
        this.cCoin = cCoin;
    }

    public Long getOldFollow() {
        return oldFollow;
    }

    public void setOldFollow(Long oldFollow) {
        this.oldFollow = oldFollow;
    }

    public Long getNewFollow() {
        return newFollow;
    }

    public void setNewFollow(Long newFollow) {
        this.newFollow = newFollow;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Long getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(Long scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSmallCover() {
        return smallCover;
    }

    public void setSmallCover(String smallCover) {
        this.smallCover = smallCover;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
