package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Jannchie
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendaVote {
    @Id
    ObjectId agendaId;
    User user;
    Byte opinion;

    public ObjectId getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(ObjectId agendaId) {
        this.agendaId = agendaId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Byte getOpinion() {
        return opinion;
    }

    public void setOpinion(Byte opinion) {
        this.opinion = opinion;
    }
}
