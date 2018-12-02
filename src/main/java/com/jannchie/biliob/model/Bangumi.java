package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "bangumi")
public class Bangumi {

  private String title;

  private List tag;

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTag(List tag) {
    this.tag = tag;
  }

  private class Data{
    private String danmaku;
    private String watch;
    private String play;
    private Integer pts;
    private Date datetime;

    public String getDanmaku() {
      return danmaku;
    }

    public String getWatch() {
      return watch;
    }

    public String getPlay() {
      return play;
    }

    public Integer getPts() {
      return pts;
    }

    public Date getDatetime() {
      return datetime;
    }
  }

  public String getTitle() {
    return title;
  }

  public List getTag() {
    return tag;
  }
}
