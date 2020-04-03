package com.jannchie.biliob.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Jannchie
 */
@Document("fans_guessing_item")
public class FansGuessingItem extends GuessingItem {
    Author author;
    Long target;
    Date reachDate;

    public Date getReachDate() {
        return reachDate;
    }

    public void setReachDate(Date reachDate) {
        this.reachDate = reachDate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
}
