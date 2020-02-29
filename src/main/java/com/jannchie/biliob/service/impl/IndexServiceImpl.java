package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.JannchieIndex;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.JannchieIndexData;
import com.jannchie.biliob.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public JannchieIndex getIndex(String keyword) {
        List<JannchieIndexData> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(new Criteria()
                                .orOperator(Criteria.where("keyword").is(keyword), Criteria.where("tag").is(keyword))),
                        Aggregation.project()
                                .and("$data.jannchie").arrayElementAt(0).as("jannchie")
                                .and("datetime").dateAsFormattedString("%Y-%m-%d").as("datetime"),
                        Aggregation.group("datetime").sum("jannchie").as("jannchie"),
                        Aggregation.project("jannchie").and("_id").as("datetime")
                )
                , Video.class, JannchieIndexData.class).getMappedResults();
        JannchieIndex jannchieIndex = new JannchieIndex();
        jannchieIndex.setName(keyword);
        jannchieIndex.setData(data);
        return jannchieIndex;
    }
}
