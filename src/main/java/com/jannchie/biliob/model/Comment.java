package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    @Id
    private ObjectId id;
    private String commentId;
    private String userId;
    private Date date;
    @NotBlank(message = "评论内容不能为空")
    private String path;
    @NotBlank(message = "评论内容不能为空")
    @Length(max = 100, message = "评论最长为100个字符")
    private String content;
    private Integer like;
    private Integer dislike;
    private ArrayList<String> likeList;
    private ArrayList<String> disLikeList;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ArrayList<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<String> likeList) {
        this.likeList = likeList;
    }

    public ArrayList<String> getDisLikeList() {
        return disLikeList;
    }

    public void setDisLikeList(ArrayList<String> disLikeList) {
        this.disLikeList = disLikeList;
    }

    public Integer getLike() {
        return likeList.size();
    }

    public Integer getDislike() {
        return disLikeList.size();
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserId(User user) {
        this.userId = userId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCommentId() {
        return id.toHexString();
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
