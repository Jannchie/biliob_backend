package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.JannchieIndex;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.JannchieIndexData;
import com.jannchie.biliob.service.IndexService;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
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
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;

    @Override

    public JannchieIndex getIndex(String keyword) {
        logger.info(keyword);
        Criteria criteria = Criteria.where("keyword").is(keyword);
        JannchieIndex jannchieIndex = new JannchieIndex();
        jannchieIndex.setName(keyword);
        List<JannchieIndexData> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("cJannchie").gt(10000)),
                        Aggregation.match(criteria),
                        Aggregation.unwind("data"),
                        Aggregation.project()
//                                .and("$data.jannchie").arrayElementAt(0).as("jannchie")
                                .and("title").as("title")
                                .and("$data.jannchie").as("jannchie")
                                .and("$data.datetime").dateAsFormattedString("%Y-%m").as("datetime"),
                        Aggregation.group("title", "datetime").first("jannchie").as("jannchie").first("datetime").as("datetime"),
                        Aggregation.group("datetime").sum("jannchie").as("jannchie"),
                        Aggregation.project("jannchie").and("_id").as("datetime"),
                        Aggregation.match(Criteria.where("jannchie").ne(0)),
                        Aggregation.sort(Sort.Direction.ASC, "datetime")
                )
                , Video.class, JannchieIndexData.class).getMappedResults();
        jannchieIndex.setData(data);
        return jannchieIndex;
    }

    @Override
    public JannchieIndex getSimIndex(String keyword) {
        logger.info(keyword);
        JannchieIndex jannchieIndex = getJannchieIndex(keyword);
        User user = UserUtils.getUser();
        boolean hasRight = (user == null || user.getExp() < 100);
        if (hasRight && jannchieIndex.getData().size() >= 31) {
            jannchieIndex.setData(jannchieIndex.getData().subList(jannchieIndex.getData().size() - 31, jannchieIndex.getData().size() - 1));
        }
        return jannchieIndex;
    }

    @Override
    @Cacheable(key = "#keyword")
    public JannchieIndex getJannchieIndex(String keyword) {
        Criteria criteria = Criteria.where("keyword").is(keyword);
        JannchieIndex jannchieIndex = new JannchieIndex();
        jannchieIndex.setName(keyword);
        List<JannchieIndexData> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("cJannchie").gt(10000)),
                        Aggregation.match(criteria),
                        Aggregation.project()
//                                .and("$data.jannchie").arrayElementAt(0).as("jannchie")
                                .and("cJannchie").as("jannchie")
                                .and("datetime").dateAsFormattedString("%Y-%m-%d").as("datetime"),
                        Aggregation.group("datetime").sum("jannchie").as("jannchie"),
                        Aggregation.project("jannchie").and("_id").as("datetime"),
                        Aggregation.match(Criteria.where("jannchie").ne(0)),
                        Aggregation.sort(Sort.Direction.ASC, "datetime")
                )
                , Video.class, JannchieIndexData.class).getMappedResults();
        jannchieIndex.setData(data);
        return jannchieIndex;
    }
}
