package com.jannchie.biliob.utils;

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

/** @author jannchie */
@Component
public class TracerScheduler {
  private static final Integer DAEAD_MINUTES = -5;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public TracerScheduler(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Scheduled(cron = "0 0/5 * * * ?")
  public void checkDeadTask() {
    Date deadDate = getDeadDate();
    mongoTemplate.updateMulti(
        Query.query(Criteria.where("update_time").lt(deadDate).and("status").ne(TracerStatus.DEAD)),
        Update.update("status", TracerStatus.DEAD).set("msg", "该任务已离线"),
        TracerTask.class);
  }

  Date getDeadDate() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MINUTE, TracerScheduler.DAEAD_MINUTES);
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
