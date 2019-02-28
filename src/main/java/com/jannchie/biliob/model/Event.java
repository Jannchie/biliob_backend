package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/** @author jannchie */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "event")
public class Event {
  private String type;
  private Integer mid;
  private String author;
  private Integer rate;
  private Date datetime;
  private Cause cause;

  public String getType() {
    return type;
  }

  public Integer getMid() {
    return mid;
  }

  public String getAuthor() {
    return author;
  }

  public Integer getRate() {
    return rate;
  }

  public Date getDatetime() {
    return datetime;
  }

  public Cause getCause() {
    return cause;
  }

  private class Cause {
    private String type;
    private Integer aid;
    private String title;

    public String getType() {
      return type;
    }

    public Integer getAid() {
      return aid;
    }

    public String getTitle() {
      return title;
    }
  }
}
