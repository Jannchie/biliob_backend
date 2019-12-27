package com.jannchie.biliob.utils.schedule;

import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
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

import static com.jannchie.biliob.constant.TimeConstant.MICROSECOND_OF_DAY;
import static com.jannchie.biliob.constant.TimeConstant.MICROSECOND_OF_MINUTES;

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


    /**
     * 每分鐘更新作者數據
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES)
    @Async
    public void updateAuthorData() {
        logger.info("[SPIDER] 每分钟更新作者数据");
        Calendar c = Calendar.getInstance();
        List<Map> authorList = mongoTemplate.find(Query.query(Criteria.where("next").lt(c.getTime())), Map.class, "author_interval");
        for (Map freqData : authorList) {
            Long mid = (Long) freqData.get("mid");
            c.add(Calendar.SECOND, (Integer) freqData.get("interval"));
            logger.info("[UPDATE] 更新作者数据：{} 下次更新时间 {}", mid, c.getTime());
            mongoTemplate.updateFirst(Query.query(Criteria.where("mid").is(mid)), Update.update("next", c.getTime()), "author_interval");
            redisOps.postAuthorCrawlTask(mid);
        }
    }

    /**
     * 每分鐘添加tag数据
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES)
    @Async
    public void addTagData() {
        List<Map> videoList = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("tag").exists(false)),
                        Aggregation.project("aid"),
                        Aggregation.limit(1000)), "video", Map.class).getMappedResults();
        redisOps.deleteTagTask();
        for (Map eachVideo : videoList) {
            Number aid = (Number) eachVideo.get("aid");
            logger.info("[UPDATE] 添加Tag：{}", aid);
            redisOps.postTagSpiderTask(aid.intValue());
        }
    }

    public void updateVideoData() {
        Calendar c = Calendar.getInstance();
        List<Map> authorList = mongoTemplate.find(Query.query(Criteria.where("next").lt(c.getTime())), Map.class, "video_interval");
        for (Map freqData : authorList) {
            Long aid = (Long) freqData.get("aid");
            logger.info("[UPDATE] 更新视频数据：{}", aid);
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
    @Scheduled(fixedDelay = MICROSECOND_OF_DAY, initialDelay = MICROSECOND_OF_DAY)
    @Async
    public void updateAuthorFreq() {
        authorService.updateObserveFreq();
    }

    /**
     * 每日執行一次
     * 更新作者增长速率
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_DAY, initialDelay = MICROSECOND_OF_DAY)
    @Async
    public void updateAuthorRate() {
        authorService.updateObserveFreq();
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
