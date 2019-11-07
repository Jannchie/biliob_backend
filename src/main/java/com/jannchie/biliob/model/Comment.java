package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    @NotBlank(message = "发布者名称不能为空")
    private String name;
    private Date date;

    @NotBlank(message = "评论内容不能为空")
    @Length(max = 100, message = "评论最长为100个字符")
    private String content;

    private Integer like;
    private Integer dislike;
    private Integer funny;

    private ArrayList<Comment> comments;

    public Comment(String name) {
        this.name = name;
        this.date = Calendar.getInstance().getTime();
        this.like = 0;
        this.dislike = 0;
        this.funny = 0;
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

    public Integer getFunny() {
        return funny;
    }

    public void setFunny(Integer funny) {
        this.funny = funny;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
