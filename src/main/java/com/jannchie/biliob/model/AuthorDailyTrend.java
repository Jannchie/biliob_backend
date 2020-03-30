package com.jannchie.biliob.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@Document("author_daily_trend")
public class AuthorDailyTrend {
    Author author;
    private Long mid;
    private Long like;
    private Long fans;
    private Long archiveView;
    private Date datetime;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public Long getArchiveView() {
        return archiveView;
    }

    public void setArchiveView(Long archiveView) {
        this.archiveView = archiveView;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
