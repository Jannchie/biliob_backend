package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.ScheduleItem;
import com.jannchie.biliob.model.TracerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** @author jannchie */
@Component
public class TracerScheduler {
  private static final Integer DEAD_MINUTES = -5;
  private final MongoTemplate mongoTemplate;
  private final RedisOps redisOps;

  @Autowired
  public TracerScheduler(MongoTemplate mongoTemplate, RedisOps redisOps) {
    this.mongoTemplate = mongoTemplate;
    this.redisOps = redisOps;
  }

  @Scheduled(cron = "0 0/5 * * * ?")
  public void checkDeadTask() {
    Date deadDate = getDeadDate();
    mongoTemplate.updateMulti(
        Query.query(
            Criteria.where("update_time").lt(deadDate).and("status").ne(TracerStatus.FINISHED)),
        Update.update("status", TracerStatus.DEAD).set("msg", "该任务已离线"),
        TracerTask.class);
  }

  @Scheduled(cron = "0 0/5 * * * ?")
  public void addCustomCrawlTaskEvery5Min() {
    postCustomVideoCrawlSchedule(3);
    postCustomAuthorCrawlSchedule(3);
  }

  @Scheduled(cron = "0 0 0/1 * * ?")
  public void addCustomCrawlTaskEvery1Hour() {
    postCustomVideoCrawlSchedule(2);
    postCustomAuthorCrawlSchedule(2);
  }

  @Scheduled(cron = "0 0 0/6 * * ?")
  public void addCustomCrawlTaskEvery6Hour() {
    postCustomVideoCrawlSchedule(1);
    postCustomAuthorCrawlSchedule(1);
  }

  private void postCustomVideoCrawlSchedule(Integer frequency) {
    List<ScheduleItem> videoScheduleList =
        mongoTemplate.find(
            Query.query(Criteria.where("frequency").is(frequency).and("type").is("video")),
            ScheduleItem.class,
            "crawl_schedule");
    for (ScheduleItem item : videoScheduleList) {
      List<String> ids = item.getIdList();
      for (String id : ids) {
        redisOps.postVideoCrawlTask(id);
      }
    }
  }

  private void postCustomAuthorCrawlSchedule(Integer frequency) {
    List<ScheduleItem> authorScheduleList =
        mongoTemplate.find(
            Query.query(Criteria.where("frequency").is(frequency).and("type").is("author")),
            ScheduleItem.class,
            "crawl_schedule");
    for (ScheduleItem item : authorScheduleList) {
      List<String> ids = item.getIdList();
      for (String id : ids) {
        redisOps.postAuthorCrawlTask(id);
      }
    }
  }

  Date getDeadDate() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MINUTE, TracerScheduler.DEAD_MINUTES);
    c.add(Calendar.HOUR, 8);
    return c.getTime();
  }

  private static class TracerStatus {
    static final Integer START = 1;
    static final Integer UPDATE = 2;
    static final Integer DEAD = 4;
    static final Integer ALIVE = 5;
    static final Integer WARNING = 6;
    static final Integer TIMEOUT = 8;
    static final Integer FINISHED = 9;
  }
}
