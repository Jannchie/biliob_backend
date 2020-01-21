package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    private Long mid;
    private String name;
    private String face;
    private String sex;
    private String official;
    private Integer level;
    private ArrayList<Data> data;
    private ArrayList<Channel> channels;
    private Rank rank;
    private Boolean focus;
    private Boolean forceFocus;
    private Integer cRate;
    private ArrayList<String> keyword;
    private Integer cFans;
    private ArrayList<Comment> comments;
    @Field("cArchive_view")
    private Long cArchiveView;
    @Field("cArticle_view")
    private Long cArticleView;
    private Long cLike;

    public Author() {
        focus = true;
    }

    public Author(Long mid) {
        this.mid = mid;
        focus = true;
    }

    public Long getcLike() {
        return cLike;
    }

    public void setcLike(Long cLike) {
        this.cLike = cLike;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Long getcArchiveView() {
        return cArchiveView;
    }

    public void setcArchiveView(Long cArchiveView) {
        this.cArchiveView = cArchiveView;
    }

    public Long getcArticleView() {
        return cArticleView;
    }

    public void setcArticleView(Long cArticleView) {
        this.cArticleView = cArticleView;
    }

    public Integer getcFans() {
        return cFans;
    }

    public void setcFans(Integer cFans) {
        this.cFans = cFans;
    }

    public ArrayList<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(ArrayList<String> keyword) {
        this.keyword = keyword;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Integer getcRate() {
        return cRate;
    }

    public void setcRate(Integer cRate) {
        this.cRate = cRate;
    }

    public Boolean getForceFocus() {
        return forceFocus;
    }

    public void setForceFocus(Boolean forceFocus) {
        this.forceFocus = forceFocus;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public void setChannel(ArrayList<Channel> channels) {
        this.channels = channels;
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

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public static class Rank {
        private Long fansRank;
        private Long archiveViewRank;
        private Long articleViewRank;
        private Long likeRank;
        private Long dFansRank;
        private Long dArchiveViewRank;
        private Long dArticleViewRank;
        private Long dLikeRank;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date updateTime;

        public Rank(Long fansRank, Long archiveViewRank, Long articleViewRank, Long likeRank, Long dFansRank, Long dArchiveViewRank, Long dArticleViewRank, Long dLikeRank, Date updateTime) {
            this.fansRank = fansRank;
            this.archiveViewRank = archiveViewRank;
            this.articleViewRank = articleViewRank;
            this.likeRank = likeRank;
            this.dFansRank = dFansRank;
            this.dArchiveViewRank = dArchiveViewRank;
            this.dArticleViewRank = dArticleViewRank;
            this.dLikeRank = dLikeRank;
            this.updateTime = updateTime;
        }

        public Long getFansRank() {
            return fansRank;
        }

        public void setFansRank(Long fansRank) {
            this.fansRank = fansRank;
        }

        public Long getArchiveViewRank() {
            return archiveViewRank;
        }

        public void setArchiveViewRank(Long archiveViewRank) {
            this.archiveViewRank = archiveViewRank;
        }

        public Long getArticleViewRank() {
            return articleViewRank;
        }

        public void setArticleViewRank(Long articleViewRank) {
            this.articleViewRank = articleViewRank;
        }

        public Long getLikeRank() {
            return likeRank;
        }

        public void setLikeRank(Long likeRank) {
            this.likeRank = likeRank;
        }

        public Long getdFansRank() {
            return dFansRank;
        }

        public void setdFansRank(Long dFansRank) {
            this.dFansRank = dFansRank;
        }

        public Long getdArchiveViewRank() {
            return dArchiveViewRank;
        }

        public void setdArchiveViewRank(Long dArchiveViewRank) {
            this.dArchiveViewRank = dArchiveViewRank;
        }

        public Long getdArticleViewRank() {
            return dArticleViewRank;
        }

        public void setdArticleViewRank(Long dArticleViewRank) {
            this.dArticleViewRank = dArticleViewRank;
        }

        public Long getdLikeRank() {
            return dLikeRank;
        }

        public void setdLikeRank(Long dLikeRank) {
            this.dLikeRank = dLikeRank;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class Data {
        private Integer fans;
        private Integer attention;
        private Integer archive;
        private Integer article;
        private Long archiveView;
        private Long articleView;
        private Long like;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date datetime;

        public Long getArchiveView() {
            return archiveView;
        }

        public void setArchiveView(Long archiveView) {
            this.archiveView = archiveView;
        }

        public Long getLike() {
            return like;
        }

        public void setLike(Long like) {
            this.like = like;
        }

        public Long getArticleView() {
            return articleView;
        }

        public void setArticleView(Long articleView) {
            this.articleView = articleView;
        }

        public Integer getFans() {
            return fans;
        }

        public void setFans(Integer fans) {
            this.fans = fans;
        }

        public Integer getAttention() {
            return attention;
        }

        public void setAttention(Integer attention) {
            this.attention = attention;
        }

        public Integer getArchive() {
            return archive;
        }

        public void setArchive(Integer archive) {
            this.archive = archive;
        }

        public Integer getArticle() {
            return article;
        }

        public void setArticle(Integer article) {
            this.article = article;
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }
    }

    private static class Channel {
        private Integer tid;
        private Integer count;
        private String name;

        public Integer getTid() {
            return tid;
        }

        public void setTid(Integer tid) {
            this.tid = tid;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
