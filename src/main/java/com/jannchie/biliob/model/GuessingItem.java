package com.jannchie.biliob.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Jannchie
 */
@Document("guessing_item")
public class GuessingItem {
    @Id
    private ObjectId id;
    private User creator;
    private String title;
    private List<String> options;
    private List<PokerChip> pokerChips;
    private Integer state;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
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

    static class PokerChip {
        private User user;
        private Integer value;
        private Integer optionIndex;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Integer getOptionIndex() {
            return optionIndex;
        }

        public void setOptionIndex(Integer optionIndex) {
            this.optionIndex = optionIndex;
        }
    }
}
