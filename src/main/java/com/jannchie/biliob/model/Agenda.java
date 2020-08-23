package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jannchie.biliob.constant.AgendaState;
import com.jannchie.biliob.constant.AgendaTypeEnum;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
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
    private AgendaTypeEnum type;

    private AgendaState state;
    private Date createTime;
    private Date finishTime;
    private Integer score;
    private Integer favorCount;
    private Integer favorScore;
    private Integer againstCount;
    private Integer againstScore;
    private List<AgendaVote> votes;

    public List<AgendaVote> getVotes() {
        return votes;
    }

    public void setVotes(List<AgendaVote> votes) {
        this.votes = votes;
    }

    public Integer getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(Integer favorCount) {
        this.favorCount = favorCount;
    }

    public Integer getFavorScore() {
        return favorScore;
    }

    public void setFavorScore(Integer favorScore) {
        this.favorScore = favorScore;
    }

    public Integer getAgainstCount() {
        return againstCount;
    }

    public void setAgainstCount(Integer againstCount) {
        this.againstCount = againstCount;
    }

    public Integer getAgainstScore() {
        return againstScore;
    }

    public void setAgainstScore(Integer againstScore) {
        this.againstScore = againstScore;
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

    public AgendaState getState() {
        return state;
    }

    public void setState(AgendaState state) {
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

    public AgendaTypeEnum getType() {
        return type;
    }

    public void setType(AgendaTypeEnum type) {
        this.type = type;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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
