package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@org.springframework.data.mongodb.core.mapping.Document(collection = "author_list")
public class AuthorList extends Document {
    @Id
    private ObjectId id;
    private ArrayList<Long> mids;
    private String creator;
    private String name;
    private Integer like;
    private Date cTime;
    private Date updateTime;
    private ArrayList<String> updateRecord;

    public AuthorList() {
        this.updateTime = Calendar.getInstance().getTime();
    }

    public AuthorList(String name, String userName) {
        this.name = name;
        this.creator = userName;
        this.updateTime = Calendar.getInstance().getTime();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Long> getMids() {
        return mids;
    }

    public void setMids(ArrayList<Long> mids) {
        this.mids = mids;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
}
