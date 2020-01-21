package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user_record")
public class UserRecord {

    @Id
    private ObjectId id;
    private String userName;
    private String datetime;
    private String message;
    private Double credit;
    private Boolean isExecuted;

    public UserRecord() {
    }

    public UserRecord(String datetime, String message, Double credit, String userName) {
        this.datetime = datetime;
        this.message = message;
        this.credit = credit;
        this.userName = userName;
        this.isExecuted = false;
    }

    public UserRecord(
            String datetime, String message, Double credit, String userName, Boolean isExecuted) {
        this.datetime = datetime;
        this.message = message;
        this.credit = credit;
        this.userName = userName;
        this.isExecuted = isExecuted;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Boolean getExecuted() {
        return isExecuted;
    }

    public void setExecuted(Boolean executed) {
        isExecuted = executed;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
