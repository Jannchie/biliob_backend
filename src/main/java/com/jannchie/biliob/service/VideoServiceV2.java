package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Video;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * @author Jannchie
 */
@Service
public class VideoServiceV2 {
    private static Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;

    public Video getVideoDetailByAid(Long aid) {
        Criteria c = Criteria.where("aid").is(aid);
        logger.info("获得AV{}的数据", aid);
        return getVideoWithAuthorDataByCriteria(c);
    }

    public Video getVideoDetailByBvid(String bvid) {
        Criteria c = Criteria.where("bvid").is(bvid);
        logger.info("获得BV{}的数据", bvid);
        return getVideoWithAuthorDataByCriteria(c);
    }

    private Video getVideoWithAuthorDataByCriteria(Criteria c) {
        Video v = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(c),
                Aggregation.lookup("author", "mid", "mid", "author_info"),
                Aggregation.unwind("author_info"),
                Aggregation.project().andExpression("{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}").as("author_info")
        ), Video.class, Video.class).getUniqueMappedResult();
        if (v == null) {
            return null;
        }
        v.setKeyword(null);
        return v;
    }
}
