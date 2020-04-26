package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jannchie
 */
@Document("group_update_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupUpdateRecord {


    private ObjectId userId;
    private User user;
    private String message;
    private Date date;
    private ObjectId gid;

    public ObjectId getGid() {
        return gid;
    }

    public void setGid(ObjectId gid) {
        this.gid = gid;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
