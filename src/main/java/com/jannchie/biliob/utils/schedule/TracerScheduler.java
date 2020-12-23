package com.jannchie.biliob.utils.schedule;

import com.jannchie.biliob.model.TracerTask;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.object.VideoIntervalRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jannchie
 */
@Component
public class TracerScheduler {
    private static final Integer DEAD_MINUTES = -5;
    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TracerScheduler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void recordSpiderQueueStatus() {
        try {
            logger.info("记录爬虫队列状态");
            Calendar c = Calendar.getInstance();
            Long authorQueueLength = mongoTemplate.count(Query.query(Criteria.where("next").lt(c.getTime())), AuthorIntervalRecord.class);
            Long videoQueueLength = mongoTemplate.count(Query.query(Criteria.where("next").lt(c.getTime())), VideoIntervalRecord.class);
            Date date = Calendar.getInstance().getTime();
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("date", date);
                put("author", authorQueueLength);
                put("video", videoQueueLength);
            }};
            mongoTemplate.insert(data, "spider_queue_status");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void checkDeadTask() {
        logger.info("检查死亡爬虫");
        Date deadDate = getDeadDate();
        mongoTemplate.updateMulti(
                Query.query(
                        Criteria.where("update_time").lt(deadDate).and("status").ne(TracerStatus.FINISHED)),
                Update.update("status", TracerStatus.DEAD).set("msg", "该任务已离线"),
                TracerTask.class);
    }

    private Date getDeadDate() {
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
