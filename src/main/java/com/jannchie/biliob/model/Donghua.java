package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "donghua")
public class Donghua {

    private String title;
    private List tag;
    private List<Data> data;
    private String cover;
    private String squareCover;
    private Integer currentPlay;
    private Integer currentPts;
    private Integer currentReview;
    private Integer currentWatch;
    private Integer currentDanmaku;

    public String getCover() {
        return cover;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getSquareCover() {
        return squareCover;
    }

    public void setSquareCover(String squareCover) {
        this.squareCover = squareCover;
    }

    public Integer getCurrentPlay() {
        return currentPlay;
    }

    public void setCurrentPlay(Integer currentPlay) {
        this.currentPlay = currentPlay;
    }

    public Integer getCurrentPts() {
        return currentPts;
    }

    public void setCurrentPts(Integer currentPts) {
        this.currentPts = currentPts;
    }

    public Integer getCurrentReview() {
        return currentReview;
    }

    public void setCurrentReview(Integer currentReview) {
        this.currentReview = currentReview;
    }

    public Integer getCurrentWatch() {
        return currentWatch;
    }

    public void setCurrentWatch(Integer currentWatch) {
        this.currentWatch = currentWatch;
    }

    public Integer getCurrentDanmaku() {
        return currentDanmaku;
    }

    public void setCurrentDanmaku(Integer currentDanmaku) {
        this.currentDanmaku = currentDanmaku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getTag() {
        return tag;
    }

    public void setTag(List tag) {
        this.tag = tag;
    }

    private class Data {
        private String danmaku;
        private String watch;
        private String play;
        private Integer pts;
        private Date datetime;

        public String getDanmaku() {
            return danmaku;
        }

        public String getWatch() {
            return watch;
        }

        public String getPlay() {
            return play;
        }

        public Integer getPts() {
            return pts;
        }

        public Date getDatetime() {
            return datetime;
        }
    }
}
