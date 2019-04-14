package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/** @author jannchie */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "realtime_fans")
public class RealTimeFans {
  private Long mid;

  private Date datetime;

  private Integer fans;

  public RealTimeFans(Long mid, Date datetime, Integer fans) {
    this.mid = mid;
    this.datetime = datetime;
    this.fans = fans;
  }

  public Long getMid() {
    return mid;
  }

  public void setMid(Long mid) {
    this.mid = mid;
  }

  public Date getDatetime() {
    return datetime;
  }

  public void setDatetime(Date datetime) {
    this.datetime = datetime;
  }

  public Integer getFans() {
    return fans;
  }

  public void setFans(Integer fans) {
    this.fans = fans;
  }
}
