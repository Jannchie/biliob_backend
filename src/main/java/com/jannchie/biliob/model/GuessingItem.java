package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
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

    public Integer getTotalUser() {
        if (pokerChips == null) {
            return 0;
        }
        return pokerChips.size();
    }

    public Double getTotalCredit() {
        Double total = 0D;
        if (pokerChips == null) {
            return total;
        }
        for (PokerChip pokerChip : pokerChips
        ) {
            total += pokerChip.getCredit();
        }
        return total;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public Date getAverageTime() {
        long totalTime = 0L;
        double totalCredit = 0D;
        if (pokerChips == null) {
            return null;
        }
        for (PokerChip pokerChip : pokerChips
        ) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            if (pokerChip.getGuessingDate().before(calendar.getTime())) {
                totalTime += pokerChip.getGuessingDate().getTime() * pokerChip.getCredit();
                totalCredit += pokerChip.getCredit();
            }
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long) (totalTime / totalCredit));
        return c.getTime();
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
