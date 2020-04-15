package com.jannchie.biliob.utils.schedule;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.VideoIntervalRecord;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.jannchie.biliob.constant.TimeConstant.*;

/**
 * @author Pan Jianqi
 */
@Component
@EnableAsync
public class SpiderScheduler {

    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;
    private final RedisOps redisOps;
    private final AuthorService authorService;
    private final VideoService videoService;


    @Autowired
    public SpiderScheduler(MongoTemplate mongoTemplate, RedisOps redisOps, AuthorService authorService, VideoService videoService) {
        this.mongoTemplate = mongoTemplate;
        this.redisOps = redisOps;
        this.authorService = authorService;
        this.videoService = videoService;
    }

    @Async
    private void reduceIntervalByDaysAndInterval(Integer days, Integer interval) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -days);
        mongoTemplate.updateMulti(Query.query(Criteria.where("date").lt(c.getTime()).and("interval").gt(interval)), Update.update("interval", interval), VideoIntervalRecord.class);
        logger.info("减少了 {}天前加入的 爬取频率到 {}", days, interval);
    }

    @Async
    private void keepMostViewVideoInterval() {
        Query q = Query.query(Criteria.where("cView").gt(5000000));
        q.fields().include("aid");
        List<Video> v = mongoTemplate.find(q, Video.class);
        for (Video video : v
        ) {
            Long aid = video.getAid();
            mongoTemplate.upsert(Query.query(Criteria.where("aid").is(aid)), Update.update("interval", SECOND_OF_DAY), VideoIntervalRecord.class);
            logger.info("每日更新av{}", aid);
        }
    }

    private void updateIntervalByDaysAndInterval(Integer days, Integer interval) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -days);
        List<VideoIntervalRecord> recordList = mongoTemplate.find(new Query(), VideoIntervalRecord.class);
        recordList.forEach(e -> {
            try {
                if (e.getBvid() != null) {
                    Query q = Query.query(Criteria.where("bvid").is(e.getBvid()));
                    q.fields().include("datetime");
                    Video v = mongoTemplate.findOne(q, Video.class);
                    if (v != null && v.getDatetime() != null && v.getDatetime().before(c.getTime())) {
                        mongoTemplate.updateFirst(Query.query(Criteria.where("bvid").is(e.getBvid())), Update.update("interval", interval), VideoIntervalRecord.class);
                        logger.info("更新了 BV{} 爬取频率", e.getBvid());
                    }
                } else {
                    Query q = Query.query(Criteria.where("aid").is(e.getAid()));
                    q.fields().include("datetime");
                    Video v = mongoTemplate.findOne(q, Video.class);
                    if (v != null && v.getDatetime() != null && v.getDatetime().before(c.getTime())) {
                        mongoTemplate.updateFirst(Query.query(Criteria.where("aid").is(e.getAid())), Update.update("interval", interval), VideoIntervalRecord.class);
                        logger.info("更新了 AV{} 爬取频率", e.getAid());
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void updateVideoData() {
        Calendar c = Calendar.getInstance();
        List<Map> authorList = mongoTemplate.find(Query.query(Criteria.where("next").lt(c.getTime())), Map.class, "video_interval");
        for (Map freqData : authorList) {
            Long aid = (Long) freqData.get("aid");
            c.add(Calendar.SECOND, (Integer) freqData.get("interval"));
            mongoTemplate.updateFirst(Query.query(Criteria.where("mid").is(aid)), Update.update("next", c.getTime()), "video_interval");
            redisOps.postAuthorCrawlTask(aid);
        }
    }

    public void updateEvent() {

    }

    public void addAuthor() {

    }

    public void addAuthorLatestVideo() {

    }

    /**
     * 每分钟執行一次
     * 更新访问频率
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES, initialDelay = MICROSECOND_OF_MINUTES)
    @Async
    public void updateAuthorFreqPerMinute() {
        authorService.updateObserveFreqPerMinute();
    }

    /**
     * 每日執行一次
     * 更新访问频率
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_DAY)
    @Async
    public void updateAuthorFreq() {
        authorService.updateObserveFreq();
    }

    @Async
    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES * 60)
    public void updateVideoInterval() {
        logger.info("计算新视频爬取频率");
        keepMostViewVideoInterval();
        reduceIntervalByDaysAndInterval(7, SECOND_OF_DAY * 7);
        reduceIntervalByDaysAndInterval(1, SECOND_OF_DAY);
    }

    /**
     * 每日執行一次
     * 更新访问频率
     * <p>
     * Scheduled(fixedDelay = MICROSECOND_OF_DAY, initialDelay = MICROSECOND_OF_DAY)
     */
    @Async
    public void newUpdateInterval() {
        logger.info("开始更新新视频爬取频率");
        updateIntervalByDaysAndInterval(1, SECOND_OF_DAY);
        updateIntervalByDaysAndInterval(7, SECOND_OF_DAY * 7);
        logger.info("更新新视频爬取频率完成");
    }

    /**
     * 每周執行一次
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_DAY * 7, initialDelay = MICROSECOND_OF_DAY)
    @Async
    public void updateVideoFreq() {
        videoService.updateObserveFreq();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void addOnlineTopVideo() {

    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    @Async
    public void updateSiteInfo() {

    }
}
