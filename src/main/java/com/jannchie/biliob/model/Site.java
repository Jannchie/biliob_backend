package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author jannchie
 */
@Document(collection = "site_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Site {
    @Id
    private ObjectId id;

    @Field("play_online")
    private int playOnline;

    @Field("web_online")
    private int webOnline;

    @Field("datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0")
    private Date datetime;

    public int getPlayOnline() {
        return playOnline;
    }

    public int getWebOnline() {
        return webOnline;
    }

    public Date getDatetime() {
        return datetime;
    }
}
