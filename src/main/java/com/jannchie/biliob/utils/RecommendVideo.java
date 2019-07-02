package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author jannchie
 */
@Component
public class RecommendVideo {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RecommendVideo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Integer> getKeyWordMapFromAidCountMap(Map<String, Integer> data) {
        return null;
    }

    public ArrayList<Video> getRecommendVideoByTagCountMap(
            Map<String, Integer> data, Integer page, Integer pagesize) {
        return null;
    }

    private Integer getScore(
            Map<String, Integer> data,
            Set<String> tagSet,
            Video video,
            Date startTime,
            Integer timeDelta) {
        Integer score = 0;
        for (String tagName : video.getTag()) {
            if (tagSet.contains(tagName)) {
                Date date = video.getDatetime();
                Integer deltaHour = Math.toIntExact(((startTime.getTime() - date.getTime()) / 3600));
                Integer rate = deltaHour / timeDelta;
                score += data.get(tagName) * rate;
            }
        }
        return score;
    }
}
