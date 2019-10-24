package com.jannchie.biliob.utils.schedule;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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


    /**
     * 每分鐘更新作者數據
     */
    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES, initialDelay = MICROSECOND_OF_MINUTES)
    @Async
    public void updateAuthorData() {
        Calendar c = Calendar.getInstance();
        List<Map> authorList = mongoTemplate.find(Query.query(Criteria.where("next").lt(c.getTime())), Map.class, "author_interval");
        for (Map freqData : authorList) {
            Long mid = (Long) freqData.get("mid");
            logger.info("[UPDATE] 更新作者数据：{}", mid);
            c.add(Calendar.SECOND, (Integer) freqData.get("interval"));
            mongoTemplate.updateFirst(Query.query(Criteria.where("mid").is(mid)), Update.update("next", c.getTime()), "author_interval");
            redisOps.postAuthorCrawlTask(mid);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void recordTop3Author() {
        logger.info("每分钟爬取前3名作者的数据");
        Query q = new Query().with(Sort.by(Sort.Direction.DESC, "cFans")).limit(3);
        q.fields().include("mid");
        List<Author> authors = mongoTemplate.find(q, Author.class, "author");
        for (Author author : authors) {
            Long mid = author.getMid();
            authorService.upsertAuthorFreq(mid, SECOND_OF_MINUTES, true);
        }
    }


    public void updateVideoData() {

    }

    public void updateEvent() {

    }

    public void addAuthor() {

    }

    public void addAuthorLatestVideo() {

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
    @Scheduled(fixedDelay = MICROSECOND_OF_DAY * 7)
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
