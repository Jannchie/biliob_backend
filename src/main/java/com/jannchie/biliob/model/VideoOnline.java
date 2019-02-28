package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

/** @author jannchie */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoOnline {
  private ObjectId id;

  private String title;

  private String author;

  private ArrayList<Data> data;

  public ObjectId getId() {
    return id;
  }

  public ArrayList<Data> getData() {
    return data;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public class Data {
    private Integer number;

    private Date datetime;

    public Integer getNumber() {
      return number;
    }

    public Date getDatetime() {
      return datetime;
    }
  }
}
