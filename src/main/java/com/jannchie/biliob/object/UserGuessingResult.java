package com.jannchie.biliob.object;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * @author Jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGuessingResult {
    private String guessingId;
    private String name;
    private Date averageDate;
    private Double credit;
    private Long score;
    private Date averageCreateTime;
    private Double revenue;
    private Long lossHour;
    private Long foreHour;

    public Long getLossHour() {
        return lossHour;
    }

    public void setLossHour(Long lossHour) {
        this.lossHour = lossHour;
    }

    public Long getForeHour() {
        return foreHour;
    }

    public void setForeHour(Long foreHour) {
        this.foreHour = foreHour;
    }

    public String getGuessingId() {
        return guessingId;
    }

    public void setGuessingId(String guessingId) {
        this.guessingId = guessingId;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAverageDate() {
        return averageDate;
    }

    public void setAverageDate(Date averageDate) {
        this.averageDate = averageDate;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Date getAverageCreateTime() {
        return averageCreateTime;
    }

    public void setAverageCreateTime(Date averageCreateTime) {

        this.averageCreateTime = averageCreateTime;
    }
}
