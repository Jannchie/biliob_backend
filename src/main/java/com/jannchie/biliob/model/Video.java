package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video {
    private Long aid;
    private String bvid;
    private Long mid;
    private String title;
    private String author;
    private String channel;
    private String subChannel;
    private Date datetime;
    private Date cDatetime;
    private String pic;
    private Boolean focus;
    private ArrayList<Data> data;
    private ArrayList<String> keyword;
    private Map rank;
    private Integer cView;
    private Integer cFavorite;
    private Integer cDanmaku;
    private Integer cReply;
    private Integer cJannchie;
    private Integer cCoin;
    private Integer cShare;
    private Integer cLike;
    private ArrayList<String> tag;
    @Field("danmaku_aggregate")
    private HashMap<Object, Object> danmakuAggregate;

    public Video(Long aid) {
        this.aid = aid;
        this.focus = true;
    }

    public Video() {
        this.focus = true;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public Integer getValue(String key) {
        switch (key) {
            case "cView":
                return getcView();
            case "cFavorite":
                return getcFavorite();
            case "cDanmaku":
                return getcDanmaku();
            case "cCoin":
                return getcCoin();
            case "cShare":
                return getcShare();
            case "cLike":
                return getcLike();
            case "cJannchie":
                return getcJannchie();
            case "cReply":
                return getcReply();
            default:
                return null;
        }
    }

    public Integer getcView() {
        return cView;
    }

    public void setcView(Integer cView) {
        this.cView = cView;
    }

    public Integer getcFavorite() {
        return cFavorite;
    }

    public void setcFavorite(Integer cFavorite) {
        this.cFavorite = cFavorite;
    }

    public Integer getcDanmaku() {
        return cDanmaku;
    }

    public void setcDanmaku(Integer cDanmaku) {
        this.cDanmaku = cDanmaku;
    }

    public Integer getcCoin() {
        return cCoin;
    }

    public void setcCoin(Integer cCoin) {
        this.cCoin = cCoin;
    }

    public Integer getcShare() {
        return cShare;
    }

    public void setcShare(Integer cShare) {
        this.cShare = cShare;
    }

    public Integer getcLike() {
        return cLike;
    }

    public void setcLike(Integer cLike) {
        this.cLike = cLike;
    }

    public ArrayList<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(ArrayList<String> keyword) {
        this.keyword = keyword;
    }

    public Map getRank() {
        return rank;
    }

    public void setRank(Map rank) {
        this.rank = rank;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public Boolean getFocus() {
        return focus;
    }

    public void setFocus(Boolean focus) {
        this.focus = focus;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public HashMap<Object, Object> getDanmakuAggregate() {
        return danmakuAggregate;
    }

    public void setDanmakuAggregate(HashMap<Object, Object> danmakuAggregate) {
        this.danmakuAggregate = danmakuAggregate;
    }

    public Integer getScore(Map<String, Integer> data) {
        Integer score = 0;
        for (String tagName : this.tag) {
            score += data.get(tagName);
        }
        return score;
    }

    public Date getcDatetime() {
        return cDatetime;
    }

    public void setcDatetime(Date cDatetime) {
        this.cDatetime = cDatetime;
    }

    public Integer getcJannchie() {
        return cJannchie;
    }

    public void setcJannchie(Integer cJannchie) {
        this.cJannchie = cJannchie;
    }

    public Integer getcReply() {
        return cReply;
    }

    public void setcReply(Integer cReply) {
        this.cReply = cReply;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public static class Data {
        private Integer view;
        private Integer favorite;
        private Integer danmaku;
        private Integer coin;
        private Integer share;
        private Integer like;
        private Integer reply;
        private Integer jannchie;
        private Integer dislike;
        private Date datetime;

        public Data() {
        }

        public Integer getReply() {
            return reply;
        }

        public void setReply(Integer reply) {
            this.reply = reply;
        }

        public Integer getJannchie() {
            return jannchie;
        }

        public void setJannchie(Integer jannchie) {
            this.jannchie = jannchie;
        }

        public Integer getView() {
            return view;
        }

        public void setView(Integer view) {
            this.view = view;
        }

        public Integer getFavorite() {
            return favorite;
        }

        public void setFavorite(Integer favorite) {
            this.favorite = favorite;
        }

        public Integer getDanmaku() {
            return danmaku;
        }

        public void setDanmaku(Integer danmaku) {
            this.danmaku = danmaku;
        }

        public Integer getCoin() {
            return coin;
        }

        public void setCoin(Integer coin) {
            this.coin = coin;
        }

        public Integer getShare() {
            return share;
        }

        public void setShare(Integer share) {
            this.share = share;
        }

        public Integer getLike() {
            return like;
        }

        public void setLike(Integer like) {
            this.like = like;
        }

        public Integer getDislike() {
            return dislike;
        }

        public void setDislike(Integer dislike) {
            this.dislike = dislike;
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }
    }
}
