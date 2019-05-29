package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.*;

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
    Set<String> set = data.keySet();
    ArrayList<Long> videoIdList = new ArrayList<>();
    for (String eachKey : set) {
      videoIdList.add(Long.valueOf(eachKey));
    }
    Aggregation a =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("aid").in(videoIdList)),
            Aggregation.project("tag", "aid"),
            Aggregation.unwind("tag"),
            Aggregation.group("tag", "aid").count().as("count"));
    List<Map> list = mongoTemplate.aggregate(a, "video", Map.class).getMappedResults();
    Map<String, Integer> result = new HashMap<>(10);
    for (Map item : list) {
      Integer tempCount = (Integer) item.get("count");
      Integer weight = data.get(String.valueOf(item.get("aid")));
      if (!result.containsKey(item.get("tag"))) {
        result.put((String) item.get("tag"), 0);
      }
      result.put((String) item.get("tag"), result.get((item.get("tag"))) + tempCount * weight);
    }
    return result;
  }

  public ArrayList<Video> getRecommendVideoByTagCountMap(
      Map<String, Integer> data, Integer page, Integer pagesize) {
    Set<String> set = data.keySet();
    Collection<Integer> values = data.values();
    String[] keys = set.toArray(new String[0]);
    List result = new ArrayList();
    Calendar c = Calendar.getInstance();
    Integer timeDelta = -24 * 7;
    c.add(Calendar.HOUR, timeDelta);
    Date startTime = c.getTime();
    AggregationResults<Video> r =
        mongoTemplate.aggregate(
            Aggregation.newAggregation(
                Aggregation.match(Criteria.where("datetime").gt(startTime).and("tag").in(keys)),
                Aggregation.project("aid", "mid", "author", "pic", "title", "tag", "datetime"),
                Aggregation.limit(pagesize),
                Aggregation.skip((long) pagesize * page)),
            "video",
            Video.class);
    List<Video> list = r.getMappedResults();
    ArrayList<Video> l = new ArrayList<>();
    l.addAll(list);
    l.sort(
        (Video a, Video b) -> {
          Integer scoreA = getScore(data, set, a, startTime, timeDelta);
          Integer scoreB = getScore(data, set, b, startTime, timeDelta);
          return scoreB - scoreA;
        });
    return l;
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
