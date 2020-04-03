package com.jannchie.biliob.model;

import com.jannchie.biliob.object.UserGuessingResult;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author Jannchie
 */
@Document("fans_guessing_item")
public class FansGuessingItem extends GuessingItem {
    Author author;
    Long target;
    Date reachDate;
    List<UserGuessingResult> result;

    public List<UserGuessingResult> getResult() {
        return result;
    }

    public void setResult(List<UserGuessingResult> result) {
        this.result = result;
    }

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
