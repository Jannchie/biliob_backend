package com.jannchie.biliob.service;

import com.jannchie.biliob.model.AuthorDailyTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class AuthorDailyTrendService {
    private MongoTemplate mongoTemplate;

    @Autowired
    public AuthorDailyTrendService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable(value = "listHistoryDailyTopTrend", key = "#key + #sort")
    public List<AuthorDailyTrend> listHistoryDailyTopTrend(String key, Integer sort) {
        String[] keys = {"fans", "like", "archiveView"};
        if (Arrays.binarySearch(keys, key) < 0) {
            return null;
        }
        return mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where(key).exists(true)),
                Aggregation.sort(Sort.by(sort == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, key)),
                Aggregation.limit(20L),
                Aggregation.lookup("author", "mid", "mid", "author"),
                Aggregation.unwind("author"),
                Aggregation.project().andExpression("{{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}}").as("author")
        ), AuthorDailyTrend.class, AuthorDailyTrend.class).getMappedResults();
    }

    @Cacheable(value = "listAuthorDailyTopTrend", key = "#date + #key + #days")
    public List<AuthorDailyTrend> listAuthorDailyTopTrend(Date date, String key, Integer days) {
        if (days > 30) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(java.util.Calendar.ZONE_OFFSET));
        Date endDate = c.getTime();
        c.add(Calendar.DATE, -days);
        Date startDate = c.getTime();
        if (!Arrays.asList("fans", "like", "archiveView").contains(key)) {
            return null;
        }
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("datetime").lte(endDate).gt(startDate).and(key).exists(true)),
                        Aggregation.group("mid").sum(key).as(key).first("mid").as("mid"),
                        Aggregation.sort(Sort.by(key).descending()),
                        Aggregation.limit(10),
                        Aggregation.lookup("author", "mid", "mid", "author"),
                        Aggregation.unwind("author"),
                        Aggregation.project().andExpression("{{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}}").as("author")
                ),
                AuthorDailyTrend.class, AuthorDailyTrend.class).getMappedResults();
    }
}
