package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.constant.VideoSortEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.model.VideoOnline;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jannchie.biliob.constant.SortEnum.PUBLISH_TIME;
import static com.jannchie.biliob.constant.SortEnum.VIEW_COUNT;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Service
@CacheConfig(cacheNames = "videoService")
public class VideoServiceImpl implements VideoService {
    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
    private static final Integer MAX_PAGE_SIZE = 10;
    private final RedisOps redisOps;
    private final VideoRepository respository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;
    private final RecommendVideo recommendVideo;

    @Autowired
    public VideoServiceImpl(
            VideoRepository respository,
            UserRepository userRepository,
            UserService userService,
            MongoTemplate mongoTemplate,
            RedisOps redisOps,
            RecommendVideo recommendVideo) {
        this.respository = respository;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
        this.redisOps = redisOps;
        this.recommendVideo = recommendVideo;
    }

    /**
     * get popular keyword
     *
     * @return
     */
    @Override
    @Cacheable(value = "popular_tag")
    public List getPopularKeyword() {
        VideoServiceImpl.logger.info("获取最流行的TAG列表");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        Aggregation a =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("datetime").gt(c.getTime())),
                        Aggregation.project("tag", "cView", "datetime"),
                        Aggregation.unwind("tag"),
                        Aggregation.group("tag").avg("cView").as("value").count().as("count"),
                        Aggregation.match(Criteria.where("count").gt(10)),
                        Aggregation.sort(Sort.Direction.DESC, "value"),
                        Aggregation.limit(50));
        return mongoTemplate.aggregate(a, "video", Map.class).getMappedResults();
    }

    /**
     * get recommend video
     *
     * @param data keyword of tag
     * @return recommend video list
     */
    @Override
    public ArrayList<Video> getRecommendVideoByTag(
            Map<String, Integer> data, Integer page, Integer pagesize) {
        VideoServiceImpl.logger.info("从tag列表获取推荐视频");
        return recommendVideo.getRecommendVideoByTagCountMap(data, page, pagesize);
    }

    /**
     * get top online video
     *
     * @return top online video
     */
    @Override
    public Map getTopOnlineVideo() {
        Aggregation a =
                Aggregation.newAggregation(
                        Aggregation.unwind("data"),
                        Aggregation.sort(Sort.Direction.DESC, "data.datetime"),
                        Aggregation.project("author", "title", "pic", "data"),
                        Aggregation.limit(20));
        List<Map> l = mongoTemplate.aggregate(a, "video_online", Map.class).getMappedResults();
        ArrayList<Map> arrayList = new ArrayList<>();
        arrayList.addAll(l);
        arrayList.sort(
                (aMap, bMap) -> {
                    Map aData = (Map) aMap.get("data");
                    Integer aNumber = Integer.valueOf((String) aData.get("number"));
                    Map bData = (Map) bMap.get("data");
                    Integer bNumber = Integer.valueOf((String) bData.get("number"));
                    return bNumber - aNumber;
                });
        return arrayList.get(0);
    }

    /**
     * get guest prefer keyword
     *
     * @param data video id visit count map
     * @return keyword map
     */
    @Override
    public Map getPreferKeyword(Map<String, Integer> data) {
        return recommendVideo.getKeyWordMapFromAidCountMap(data);
    }

    @Override
    public Video getAggregatedData(Long aid) {
        Aggregation a =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("aid").is(aid)),
                        Aggregation.unwind("$data"),
                        Aggregation.project()
                                .andExpression("year($data.datetime)")
                                .as("year")
                                .andExpression("month($data.datetime)")
                                .as("month")
                                .andExpression("dayOfMonth($data.datetime)")
                                .as("day")
                                .andInclude(
                                        "data",
                                        "aid",
                                        "mid",
                                        "author",
                                        "title",
                                        "focus",
                                        "forceFocus",
                                        "pic",
                                        "cCoin",
                                        "cDanmaku",
                                        "cShare",
                                        "cLike",
                                        "cFavorite",
                                        "tag",
                                        "cView",
                                        "cDatetime",
                                        "datetime",
                                        "channel",
                                        "danmaku_aggregate",
                                        "subChannel"),
                        Aggregation.group(
                                "year",
                                "month",
                                "day",
                                "aid",
                                "mid",
                                "author",
                                "title",
                                "focus",
                                "forceFocus",
                                "pic",
                                "cCoin",
                                "cDanmaku",
                                "danmaku_aggregate",
                                "cShare",
                                "cLike",
                                "cFavorite",
                                "cView",
                                "cDatetime",
                                "datetime",
                                "channel",
                                "tag",
                                "subChannel")
                                .max("data")
                                .as("data"),
                        Aggregation.group(
                                "aid",
                                "mid",
                                "author",
                                "title",
                                "focus",
                                "forceFocus",
                                "pic",
                                "cCoin",
                                "cDanmaku",
                                "cShare",
                                "cLike",
                                "cFavorite",
                                "cView",
                                "cDatetime",
                                "datetime",
                                "tag",
                                "danmaku_aggregate",
                                "channel",
                                "subChannel")
                                .push("data")
                                .as("data"));
        return mongoTemplate.aggregate(a, "video", Video.class).getMappedResults().get(0);
    }

    private void addVideoVisit(Long aid) {
        String finalUserName = BiliOBUtils.getUserName();
        Map data = BiliOBUtils.getVisitData(finalUserName, aid);
        VideoServiceImpl.logger.info("用户[{}]查询aid[{}]的详细数据", finalUserName, aid);
        mongoTemplate.insert(data, "video_visit");
    }

    @Override
    public Video getVideoDetails(Long aid, Integer type) {
        addVideoVisit(aid);
        Video video;
        switch (type) {
            case 0:
                video = respository.findByAid(aid);
                break;
            default:
                video = getAggregatedData(aid);
        }
        HashMap rankTable =
                mongoTemplate.findOne(
                        Query.query(Criteria.where("name").is("video_rank")), HashMap.class, "rank_table");
        String[] keys = {"cCoin", "cView", "cDanmaku", "cLike", "cShare", "cFavorite"};
        HashMap<String, Object> rank = new HashMap<>(6);
        video.setRank(rank);
        Long number = mongoTemplate.count(Query.query(Criteria.where("cCoin").gt(video.getValue("cCoin"))), "video");
        System.out.println(number);
        if (rankTable != null) {
            for (String eachKey : keys) {
                String cKey = eachKey + "Rank";
                HashMap map = (HashMap) rankTable.get(eachKey);
                if (map.containsKey(video.getTitle())) {
                    rank.put(cKey, map.get(video.getTitle()));
                } else {
                    ArrayList valueArray = (ArrayList) map.get("rate");
                    Integer cValue = video.getValue(eachKey);
                    for (Integer i = 1; i < valueArray.size(); i++) {
                        Integer rangeBValue = (Integer) valueArray.get(i);
                        Integer rangeTValue = (Integer) valueArray.get(i - 1);
                        if ((cValue != null) && (Integer) valueArray.get(0) < cValue) {
                            Long value = mongoTemplate.count(Query.query(Criteria.where(eachKey).gt(video.getValue(eachKey))), "video");
                            rank.put(cKey, value);
                            break;
                        }
                        if ((cValue > rangeBValue) && ((Integer) valueArray.get(0) > cValue)) {
                            String pKey = eachKey.replace('c', 'p') + "Rank";
                            String value = String.format(
                                    "%.2f",
                                    (float)
                                            (i - 1 + (cValue - rangeBValue) / (float) (rangeTValue - rangeBValue)));
                            rank.put(pKey, value);
                            break;
                        }

                    }

                }
            }
            rank.put("updateTime", video.getcDatetime());
            video.setRank(rank);
        }
        return video;
    }

    @Override
    public ResponseEntity<Message> postVideoByAid(Long aid) throws UserAlreadyFavoriteVideoException {
        userService.addFavoriteVideo(aid);
        if (respository.findByAid(aid) != null) {
            return new ResponseEntity<>(new Message(400, "系统已经观测了该视频"), HttpStatus.BAD_REQUEST);
        }
        VideoServiceImpl.logger.info(aid);
        respository.save(new Video(aid));
        redisOps.postVideoCrawlTask(aid);
        return new ResponseEntity<>(new Message(200, "观测视频成功"), HttpStatus.OK);
    }

    @Override
    @Cacheable(value = "video_slice", key = "#aid + #text + #page + #pagesize + #sort + #days")
    public MySlice<Video> getVideo(
            Long aid, String text, Integer page, Integer pagesize, Integer sort, Integer days) {

        Calendar c = Calendar.getInstance();

        String sortKey = VideoSortEnum.getKeyByFlag(sort);
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            pagesize = PageSizeEnum.BIG_SIZE.getValue();
        }

        if (!(aid == -1)) {
            VideoServiceImpl.logger.info(aid);
            return new MySlice<>(
                    respository.searchByAid(
                            aid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
        } else if (!Objects.equals(text, "")) {
            if (InputInspection.isId(text)) {
                return new MySlice<>(
                        respository.searchByAid(
                                Long.valueOf(text),
                                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            }
            VideoServiceImpl.logger.info(text);
            // get text
            String[] textArray = text.split(" ");
            MySlice<Video> mySlice;
            if (textArray.length != 1) {
                mySlice =
                        new MySlice<>(
                                respository.findByKeywordContaining(
                                        textArray,
                                        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            } else {
                mySlice =
                        new MySlice<>(
                                respository.findByOneKeyword(
                                        textArray[0],
                                        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            }
            if (mySlice.getContent().isEmpty()) {
                for (String eachText : textArray) {
                    HashMap<String, String> map = new HashMap<>(1);
                    map.put("aid", eachText);
                    mongoTemplate.insert(map, "search_word");
                }
            }
            return mySlice;
        } else {

            if (days >= 0 && days <= 30) {
                VideoServiceImpl.logger.info("获取指定日期内的视频数据");
                c.add(Calendar.DATE, -days);
                Date date = c.getTime();
                return new MySlice<>(
                        respository.findAllByDatetimeGreaterThan(
                                date, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            } else {
                VideoServiceImpl.logger.info("获取全部视频数据");
                return new MySlice<>(
                        respository.findVideoBy(
                                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            }
        }
    }

    @Override
    @Cacheable(value = "author_other_video", key = "#aid + #mid + #page + #pagesize")
    public MySlice<Video> getAuthorOtherVideo(Long aid, Long mid, Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.SMALL_SIZE.getValue()) {
            pagesize = PageSizeEnum.SMALL_SIZE.getValue();
        }
        VideoServiceImpl.logger.info("获取作者其他数据");
        return new MySlice<>(
                respository.findAuthorOtherVideo(
                        aid, mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cView"))));
    }

    /**
     * Get author top video.
     *
     * @param mid      author id
     * @param page     no use
     * @param pagesize the number of displayed video
     * @param sort     0: order by view || 1: order by publish datetime.
     * @return slice of author's video || null: param error
     */
    @Override
    @Cacheable(value = "author_top_video", key = "#mid + #page + #pagesize + #sort")
    public MySlice<Video> getAuthorTopVideo(Long mid, Integer page, Integer pagesize, Integer sort) {
        if (pagesize >= VideoServiceImpl.MAX_PAGE_SIZE) {
            pagesize = VideoServiceImpl.MAX_PAGE_SIZE;
        }
        Sort videoSort;
        if (Objects.equals(sort, VIEW_COUNT.getValue())) {
            videoSort = new Sort(Sort.Direction.DESC, "cView");
        } else if (Objects.equals(sort, PUBLISH_TIME.getValue())) {
            videoSort = new Sort(Sort.Direction.DESC, "datetime");
        } else {
            return null;
        }

        Slice<Video> video =
                respository.findAuthorTopVideo(mid, PageRequest.of(page, pagesize, videoSort));
        VideoServiceImpl.logger.info("获取mid:{} 播放最多的视频", mid);
        return new MySlice<>(video);
    }

    /**
     * Get my video.
     *
     * @return the latest of my video
     */
    @Override
    public Video getMyVideo() {
        Query q = new Query(where("mid").is(1850091)).with(new Sort(Sort.Direction.DESC, "datetime"));
        q.fields().exclude("data");
        Video video = mongoTemplate.findOne(q, Video.class);
        VideoServiceImpl.logger.info("获取广告");
        return video;
    }

    /**
     * Get top online video in one day.
     *
     * @return top online video response.
     */
    @Override
    public ResponseEntity listOnlineVideo() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.DATE, -1);
        Aggregation aggregation =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("data.datetime").gt(todayStart.getTime())),
                        Aggregation.limit(20),
                        Aggregation.project("title", "author")
                                .and("data")
                                .filter(
                                        "item",
                                        ComparisonOperators.Gte.valueOf("$$item.datetime")
                                                .greaterThanEqualToValue(todayStart.getTime()))
                                .as("$data"));
        AggregationResults<VideoOnline> aggregationResults =
                mongoTemplate.aggregate(aggregation, "video_online", VideoOnline.class);
        List<VideoOnline> videoOnlineList = aggregationResults.getMappedResults();
        return new ResponseEntity<>(videoOnlineList, HttpStatus.OK);
    }

    /**
     * Get the number of video be observed.
     *
     * @return the number of video be observed.
     */
    @Override
    public Long getNumberOfVideo() {
        return mongoTemplate.count(new Query(), "video");
    }

    @Override
    public Map getRankTable() {
        return mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("video_rank")), Map.class, "rank_table");
    }

    /**
     * get most popular tag
     *
     * @return most popular tag
     */
    @Override
    public List listMostPopularTag() {
        return mongoTemplate
                .aggregate(
                        Aggregation.newAggregation(
                                Aggregation.unwind("tag"),
                                Aggregation.group("tag").sum("cView").as("totalView"),
                                Aggregation.sort(Sort.Direction.DESC, "totalView"),
                                Aggregation.limit(100)),
                        "video",
                        Map.class)
                .getMappedResults();
    }
}
