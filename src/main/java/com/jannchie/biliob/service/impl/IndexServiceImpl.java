package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.JannchieIndex;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.JannchieIndexData;
import com.jannchie.biliob.service.IndexService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.BiliobUtils;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Jannchie
 */
@Service
public class IndexServiceImpl implements IndexService {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    VideoService videoService;
    @Autowired
    BiliobUtils biliobUtils;
    @Resource
    private IndexService self;

    private Set<String> visiting = new HashSet<>();

    @Override
    public JannchieIndex getIndex(String keyword) {
        logger.info(keyword);
        Criteria criteria = Criteria.where("keyword").is(keyword);
        JannchieIndex jannchieIndex = new JannchieIndex();
        jannchieIndex.setName(keyword);
        List<JannchieIndexData> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("cJannchie").gt(1000000)),
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
        JannchieIndex jannchieIndex = self.getJannchieIndex(keyword);
        if (jannchieIndex == null) {
            return null;
        }
        User user = UserUtils.getUser();
        boolean hasRight = (user == null || user.getExp() < 100);
        if (hasRight && jannchieIndex.getData().size() >= 31) {
            jannchieIndex.setData(jannchieIndex.getData().subList(jannchieIndex.getData().size() - 31, jannchieIndex.getData().size() - 1));
        }
        return jannchieIndex;
    }

    @Override
    @Cacheable(value = "index", key = "#keyword")
    public JannchieIndex getJannchieIndex(String keyword) {
        if (visiting.contains(keyword)) {
            return null;
        }
        visiting.add(keyword);
        Criteria criteria = Criteria.where("keyword").is(keyword).and("cJannchie").gt(100000);
        JannchieIndex jannchieIndex = new JannchieIndex();
        jannchieIndex.setName(keyword);
        List<JannchieIndexData> data = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(criteria),
                        Aggregation.sort(Sort.Direction.DESC, "cJannchie"),
                        Aggregation.limit(64),
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
        // TODO: ADD Visit Record
        visiting.remove(keyword);
        return jannchieIndex;
    }

    @Override
    @Cacheable(value = "jannchie-index-recently-rank")
    public List<?> getRecentlyRank() {
        logger.info("获取近期(7日内)热门指数");
        List<HashMap<?, ?>> data = (List<HashMap<?, ?>>) videoService.getPopularTag();
        List<HashMap> modifiableList = new ArrayList<>(data);
        modifiableList.sort(Comparator.comparing(e -> (Integer) e.get("value")));

        Object[] tagArray = modifiableList.stream().map(e -> (String) e.get("_id")).toArray();
        ArrayList<Object> al = new ArrayList<>(Arrays.asList(tagArray).subList(0, 8));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);
        AggregationResults<?> ar = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.unwind("tag"),
                        Aggregation.match(Criteria.where("tag").in(al).and("datetime").gt(c.getTime())),
                        Aggregation.group("tag").sum("cJannchie").as("jannchie"),
                        Aggregation.project("jannchie").and("_id").as("tag")
                ),
                Video.class,
                HashMap.class
        );
        return ar.getMappedResults();
    }
}
