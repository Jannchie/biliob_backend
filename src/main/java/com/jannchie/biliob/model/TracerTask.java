package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/** @author jannchie */
@Document(collection = "tracer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TracerTask {
  @Field("class_name")
  private String className;

  @Field("computer_name")
  private String computerName;

  private String msg;

  @Field("task_name")
  private String taskName;

  private Integer status;

  @Field("total_value")
  private Integer totalValue;

  @Field("current_value")
  private Integer currentValue;

  @Field("crawl_count")
  private Integer crawlCount;

  @Field("crawl_failed")
  private Integer crawlFailed;

  @Field("start_time")
  private Date startTime;

  @Field("update_time")
  private Date updateTime;

  public Integer getCrawlCount() {
    return crawlCount;
  }

  public void setCrawlCount(Integer crawlCount) {
    this.crawlCount = crawlCount;
  }

  public Integer getCrawlFailed() {
    return crawlFailed;
  }

  public void setCrawlFailed(Integer crawlFailed) {
    this.crawlFailed = crawlFailed;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getComputerName() {
    return computerName;
  }

  public void setComputerName(String computerName) {
    this.computerName = computerName;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(Integer totalValue) {
    this.totalValue = totalValue;
  }

  public Integer getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(Integer currentValue) {
    this.currentValue = currentValue;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }
}
