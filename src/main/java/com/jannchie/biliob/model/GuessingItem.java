package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author Jannchie
 */
@Document("guessing_item")
public class GuessingItem {
    @Id
    private ObjectId guessingId;
    private Integer type;
    private User creator;
    private String title;
    private List<PokerChip> pokerChips;
    private Integer state;
    private Double totalCredit;
    private Integer totalUser;
    private Date averageTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PokerChip> getPokerChips() {
        return pokerChips;
    }

    public void setPokerChips(List<PokerChip> pokerChips) {
        this.pokerChips = pokerChips;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getGuessingId() {
        return guessingId.toHexString();
    }

    public void setGuessingId(ObjectId guessingId) {
        this.guessingId = guessingId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public Date getAverageTime() {
        return this.averageTime;
    }

    public void setAverageTime(Date averageTime) {
        this.averageTime = averageTime;
    }

    public Double getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Double totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Integer getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Integer totalUser) {
        this.totalUser = totalUser;
    }

    public static class PokerChip {
        private User user;
        private Double credit;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private Date guessingDate;
        private Date createTime;

        public Date getGuessingDate() {
            return guessingDate;
        }

        public void setGuessingDate(Date guessingDate) {
            this.guessingDate = guessingDate;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Double getCredit() {
            return credit;
        }

        public void setCredit(Double credit) {
            this.credit = credit;
        }

    }
}
