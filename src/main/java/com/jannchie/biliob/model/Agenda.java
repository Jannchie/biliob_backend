package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agenda {
    @Id
    private String id;
    private User creator;
    @NotNull
    @Length(max = 30, min = 5)
    private String title;
    @NotNull
    @Length(max = 233, min = 5)
    private String desc;
    @NotNull
    private Byte type;

    private Byte state;
    private Date createTime;
    private Date finishTime;
    private Date updateTime;
    private Integer score;
    private Integer favorCount;
    private Double favorScore;
    private Integer againstCount;
    private Double againstScore;
    private List<AgendaVote> votes;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(Integer favorCount) {
        this.favorCount = favorCount;
    }

    public Double getFavorScore() {
        return favorScore;
    }

    public void setFavorScore(Double favorScore) {
        this.favorScore = favorScore;
    }

    public Integer getAgainstCount() {
        return againstCount;
    }

    public void setAgainstCount(Integer againstCount) {
        this.againstCount = againstCount;
    }

    public Double getAgainstScore() {
        return againstScore;
    }

    public void setAgainstScore(Double againstScore) {
        this.againstScore = againstScore;
    }

    public List<AgendaVote> getVotes() {
        return votes;
    }

    public void setVotes(List<AgendaVote> votes) {
        this.votes = votes;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
