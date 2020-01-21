package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

/**
 * @author jannchie
 */
@Document(collection = "question")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {
    @Id
    private ObjectId id;

    @Field("question")
    @NotBlank(message = "问题不能为空！")
    @Length(max = 30, min = 5, message = "问题不能超过30个字符！最短为5个字符！")
    private String question;

    @Field("answer")
    private String answer;

    @Field("user")
    private String user;

    @Field("statues")
    private String statues;

    public Question() {
    }

    public Question(String question, String user) {
        this.question = question;
        this.user = user;
        this.statues = "未处理";
    }

    public Question(String question) {
        this.question = question;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatues() {
        return statues;
    }

    public void setStatues(String statues) {
        this.statues = statues;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
