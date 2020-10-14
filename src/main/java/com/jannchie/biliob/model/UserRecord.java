package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.constant.CreditConstant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user_record")
public class UserRecord {

    @Id
    private ObjectId id;
    private String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String datetime;
    private Date createTime;
    private Date executeTime;
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

    public UserRecord(CreditConstant creditConstant, String param, String userName) {
        Date d = Calendar.getInstance().getTime();
        this.datetime = d.toString();
        this.message = creditConstant.getMsg(param);
        this.credit = creditConstant.getValue();
        this.userName = userName;
        this.isExecuted = false;
        this.createTime = d;
        this.executeTime = d;
        this.isExecuted = true;
    }

    public UserRecord(CreditConstant creditConstant, String param, String userName, Boolean isExecuted) {
        Date d = Calendar.getInstance().getTime();
        this.datetime = d.toString();
        this.message = creditConstant.getMsg(param);
        this.credit = creditConstant.getValue();
        this.userName = userName;
        this.isExecuted = false;
        this.createTime = d;
        this.isExecuted = isExecuted;
        if (isExecuted) {
            this.executeTime = d;
        }
    }

    public UserRecord(
            String datetime, String message, Double credit, String userName, Boolean isExecuted) {
        this.datetime = datetime;
        this.message = message;
        this.credit = credit;
        this.userName = userName;
        this.isExecuted = isExecuted;
    }

    public UserRecord(User user, CreditConstant creditConstant, String message) {
        this(user, creditConstant, message, true);
    }

    public UserRecord(User user, CreditConstant creditConstant, String message, Boolean isExecuted) {
        this.credit = creditConstant.getValue();
        this.message = message;
        Date d = Calendar.getInstance().getTime();
        this.datetime = d.toString();
        this.createTime = d;
        this.userName = user.getName();
        this.isExecuted = isExecuted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
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
