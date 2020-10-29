package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.DbFields;
import com.jannchie.biliob.model.*;
import com.jannchie.biliob.utils.BiliobUtils;
import com.jannchie.biliob.utils.UserUtils;
import com.mongodb.client.MongoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
@Controller
public class VideoServiceV3 {
    private static Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoClient mongoClient;
    @Autowired
    BiliobUtils biliobUtils;

    private void addVideoVisit(Long aid, String type) {
        String finalUserName = biliobUtils.getUserName();
        logger.info("V3：用户[{}]查询aid[{}]的{}数据", finalUserName, aid, type);
        VideoVisit vv = new VideoVisit();
        vv.setAid(aid);
        vv.setDate(Calendar.getInstance().getTime());
        vv.setName(finalUserName);
        mongoTemplate.save(vv);
    }

    private void addVideoVisit(String bvid, String type) {
        String finalUserName = biliobUtils.getUserName();
        logger.info("V3：用户[{}]查询aid[{}]的{}数据", finalUserName, bvid, type);
        VideoVisit vv = new VideoVisit();
        vv.setBvid(bvid);
        vv.setDate(Calendar.getInstance().getTime());
        vv.setName(finalUserName);
        mongoTemplate.save(vv);
    }


    public VideoInfo getVideoInfo(Long aid) {
        Criteria c = Criteria.where("aid").is(aid);
        addVideoVisit(aid, "信息");
        return getVideoInfoByCriteria(c);
    }

    public VideoInfo getVideoInfo(String bvid) {
        Criteria c = Criteria.where("bvid").is(bvid);
        addVideoVisit(bvid, "信息");
        return getVideoInfoByCriteria(c);
    }

    public List<VideoStat> listVideoStat(Long aid) {
        addVideoVisit(aid, "历史");
        Criteria c = Criteria.where("aid").is(aid);
        return getVideoStat(c);
    }

    private List<VideoStat> getVideoStat(Criteria c) {
        return mongoTemplate.find(Query.query(c), VideoStat.class);
    }

    public List<VideoStat> listVideoStat(String bvid) {
        addVideoVisit(bvid, "历史");
        Criteria c = Criteria.where("bvid").is(bvid);
        return getVideoStat(c);
    }


    private VideoInfo getVideoInfoByCriteria(Criteria c) {
        return mongoTemplate.findOne(Query.query(c), VideoInfo.class);
    }

    public Document getAverage(Integer tid, Long mid, Long pubdate) {
        Criteria c = new Criteria();
        if (mid != -1) {
            c.and("owner.mid").is(mid);
        }
        if (tid != -1) {
            c.and("tid").is(tid);
        }
        if (pubdate != -1) {
            c.and("pubdate").lt(pubdate);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(c),
                Aggregation.sort(Sort.Direction.DESC, "pubdate"),
                Aggregation.limit(5000),
                Aggregation.group()
                        .avg("stat.view").as("view")
                        .avg("stat.coin").as("coin")
                        .avg("stat.favorite").as("favorite")
                        .avg("stat.reply").as("reply")
                        .avg("stat.danmaku").as("danmaku")
                        .avg("stat.like").as("like")
                        .avg("stat.share").as("share")
        );
        return mongoTemplate.aggregate(aggregation, VideoInfo.class, Document.class).getUniqueMappedResult();
    }

    public List<VideoInfo> listAd() {
        Query q = Query.query(new Criteria().orOperator(Criteria.where(DbFields.OWNER_MID).is(1850091L), Criteria.where(DbFields.MID).is(492106967L)));
        q.with(Sort.by(DbFields.PUBDATE).descending());
        q.limit(5);
        q.fields().include(DbFields.TITLE).include(DbFields.PUBDATE).include(DbFields.OWNER).include(DbFields.STAT);
        return mongoTemplate.find(q, VideoInfo.class);
    }

    public List<VideoInfo> listSearch(String word, Integer page, Integer size, String sort, Long day) {
        if (!Arrays.asList("view", "coin", "reply", "danmaku", "favorite", "like", "share").contains(sort)) {
            sort = "view";
        }
        if (page > 50) {
            page = 50;
        }
        if (size > 20) {
            size = 20;
        }
        Query q = new Query().with(PageRequest.of(page, size, Sort.by("stat." + sort).descending()));
        if (!"".equals(word)) {
            q.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(word.split(" ")));
        }
        if (day != 0) {
            if (day > 30) {
                day = 30L;
            }
            long ctime = Calendar.getInstance().getTimeInMillis() / 1000;
            ctime -= 86400 * day;
            q.addCriteria(Criteria.where("ctime").gt(ctime));
        }
        return mongoTemplate.find(q, VideoInfo.class);
    }

    public List<VideoInfo> listAuthorVideo(Long mid, String sort) {
        if (!Arrays.asList("view", "ctime").contains(sort)) {
            sort = "view";
        }
        if (!"ctime".equals(sort)) {
            sort = "stat." + sort;
        }
        return mongoTemplate.find(Query.query(Criteria.where(DbFields.OWNER_MID).is(mid)).with(Sort.by(sort).descending()).limit(10), VideoInfo.class);
    }

    @Cacheable(value = "listTopicAuthor", key = "#topic + #limit)")
    public List<Document> listTopicAuthor(String topic, Integer limit) {
        if ("".equals(topic)) {
            return null;
        }
        if (limit > 200) {
            limit = 200;
        }
        return mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(TextCriteria.forDefaultLanguage().matching(topic)),
                Aggregation.group(DbFields.OWNER_MID)
                        .first(DbFields.OWNER_NAME).as(DbFields.NAME)
                        .first(DbFields.OWNER_MID).as(DbFields.MID)
                        .first(DbFields.OWNER_FACE).as(DbFields.FACE)
                        .sum(DbFields.STAT_VIEW).as(DbFields.VALUE)
                        .count().as(DbFields.COUNT),
                Aggregation.match(Criteria.where(DbFields.COUNT).gt(8)),
                Aggregation.sort(Sort.by(DbFields.VALUE).descending()),
                Aggregation.limit(limit)
        ), VideoInfo.class, Document.class).getMappedResults();
    }

    public List<VideoInfo> listFavoriteVideo() {
        User user = UserUtils.getUser();
        if (user == null) {
            return null;
        }
        return mongoTemplate.find(Query.query(Criteria.where(DbFields.AID).in(user.getFavoriteAid())), VideoInfo.class);
    }

    @Cacheable(value = "listKeywordIndex", key = "#kw)")
    public List<Document> listKeywordIndex(String kw) {
        List<Document> docs = this.listTopicAuthor(kw, 200);
        Object[] midList = docs.stream().map(document -> document.get(DbFields.MID)).toArray();
        Calendar c = Calendar.getInstance();
        c.set(2018, Calendar.OCTOBER, 1);
        return mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where(DbFields.MID).in(midList)),
                Aggregation.lookup("author_daily_trend", "mid", "mid", "history"),
                Aggregation.unwind("history"),
                Aggregation.project()
                        .and("history.datetime").as("history.datetime")
                        .and("history.fans").absoluteValue().as("history.fans").and("history.datetime"),
                Aggregation.group("history.datetime").sum("history.fans").as("val"),
                Aggregation.match(Criteria.where("_id").gt(c.getTime())),
                Aggregation.sort(Sort.by("_id").ascending())
        ), Author.class, Document.class).getMappedResults();
    }

    @Cacheable(value = "listTopTag", key = "#d")
    public List<Document> listTopTag(Integer d) {
        if (d < 0 || d > 30) {
            d = 3;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -d);
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where(DbFields.CTIME).gt(c.getTimeInMillis() / 1000)),
                        Aggregation.unwind(DbFields.TAG),
                        Aggregation.group(DbFields.TAG).sum(DbFields.STAT_JANNCHIE).as(DbFields.VALUE),
                        Aggregation.sort(Sort.by(DbFields.VALUE).descending()),
                        Aggregation.limit(100)), VideoInfo.class, Document.class).getMappedResults();
    }
}
