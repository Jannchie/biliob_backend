package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class Comment {
    @Id
    private String commentId;
    private String parentId;
    private ObjectId userId;
    private List<Comment> replies;
    private Date date;
    @NotBlank(message = "路径不能为空")
    private String path;
    @NotBlank(message = "观测记录内容不能为空")
    @Length(max = 100, message = "观测记录最长为100个字符")
    private String content;
    private List<ObjectId> likeList;
    private List<ObjectId> disLikeList;
    private User user;
    private Integer like;
    private boolean liked;

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public List<ObjectId> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<ObjectId> likeList) {
        this.likeList = likeList;
    }

    public List<ObjectId> getDisLikeList() {
        return disLikeList;
    }

    public void setDisLikeList(List<ObjectId> disLikeList) {
        this.disLikeList = disLikeList;
    }

    public Integer getDislike() {
        return disLikeList.size();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private static class Reply {
        @Id
        private String commentId;
        private ArrayList<Reply> replies;
        private Date date;
        @NotBlank(message = "观测记录内容不能为空")
        @Length(max = 100, message = "观测记录最长为100个字符")
        private String content;
        private User user;

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public ArrayList<Reply> getReplies() {
            return replies;
        }

        public void setReplies(ArrayList<Reply> replies) {
            this.replies = replies;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}
