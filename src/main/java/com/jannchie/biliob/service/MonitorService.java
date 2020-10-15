package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Author;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jannchie.biliob.constant.DbFields.DATETIME;

/**
 * @author Jannchie
 */
@Service
public class MonitorService {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<Document> getCrawlRateAuthor() {
        Aggregation a = getAggregation();
        return mongoTemplate.aggregate(a, Author.Data.class, Document.class).getMappedResults();
    }

    private Aggregation getAggregation() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, -1);
        Date startDate = c.getTime();
        return Aggregation.newAggregation(
                Aggregation.match(Criteria.where(DATETIME).gt(startDate)),
                Aggregation.project("id").and(DATETIME).dateAsFormattedString("%Y-%m-%d %H:%M").as("date"),
                Aggregation.group("date").count().as("count"),
                Aggregation.sort(Sort.by("_id").ascending())
        );
    }

    public List<Document> getCrawlRateVideo() {
        Aggregation a = getAggregation();
        return mongoTemplate.aggregate(a, "video_stat", Document.class).getMappedResults();
    }
}
