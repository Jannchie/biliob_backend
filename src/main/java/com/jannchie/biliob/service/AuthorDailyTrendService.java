package com.jannchie.biliob.service;

import com.jannchie.biliob.model.AuthorDailyTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

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

    public List<AuthorDailyTrend> listAuthorDailyTopTrend(Date date, String key) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(java.util.Calendar.ZONE_OFFSET));
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("datetime").is(c.getTime())),
                        Aggregation.sort(Sort.by(key).descending()),
                        Aggregation.limit(10),
                        Aggregation.lookup("author", "mid", "mid", "author"),
                        Aggregation.unwind("author"),
                        Aggregation.project().andExpression("{data: 0, keyword: 0, rank: 0, focus:0, forceFocus:  0}").as("author")
                ),
                AuthorDailyTrend.class, AuthorDailyTrend.class).getMappedResults();
    }

}
