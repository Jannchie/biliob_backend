package com.jannchie.biliob.utils.schedule;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AuthorService;
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

import static java.lang.Thread.sleep;

/**
 * @author Pan Jianqi
 */
@Component
@EnableAsync
public class SpiderScheduler {
    private static final int SECONDS_OF_DAY = 86400;
    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;
    private final RedisOps redisOps;
    private final AuthorService authorService;
    private Long mid;
    private Integer interval;


    @Autowired
    public SpiderScheduler(MongoTemplate mongoTemplate, RedisOps redisOps, AuthorService authorService) {
        this.mongoTemplate = mongoTemplate;
        this.redisOps = redisOps;
        this.authorService = authorService;

    }


    /**
     * 每分鐘更新作者數據
     */
    @Scheduled(fixedDelay = 1000 * 60)
    @Async
    public void updateAuthorData() {
        logger.info("[CHECK]检查爬虫任务");
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

    @Scheduled(fixedDelay = 1000)
    @Async
    public void test1() throws InterruptedException {

        sleep(1000);

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
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    @Async
    public void updateObserveFreq() {
        logger.info("[UPDATE] 调整观测频率");
        // 十万粉丝以上：正常观测
        List<Author> authorList = getAuthorFansGt(100000);
        for (Author author : authorList
        ) {
            logger.debug("mid:{}", author.getMid());
            authorService.upsertAuthorFreq(author.getMid(), SECONDS_OF_DAY);
        }

        // 百万粉以上：高频观测
        authorList = getAuthorFansGt(1000000);
        for (Author author : authorList
        ) {
            logger.debug("mid:{}", author.getMid());
            authorService.upsertAuthorFreq(author.getMid(), SECONDS_OF_DAY / 4);
        }
        // 人为设置：强行观测
        Query q = Query.query(Criteria.where("forceFocus").is(true));
        q.fields().include("mid");
        authorList = mongoTemplate.find(q, Author.class, "author");
        for (Author author : authorList
        ) {
            logger.debug("mid:{}", author.getMid());
            authorService.upsertAuthorFreq(author.getMid(), SECONDS_OF_DAY / 4);
        }
    }


    private List<Author> getAuthorFansGt(Integer gt) {
        Query q = Query.query(Criteria.where("cFans").gt(gt));
        q.fields().include("mid");
        return mongoTemplate.find(q, Author.class, "author");
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
