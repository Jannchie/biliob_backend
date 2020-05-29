package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.BiliobConstant;
import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.constant.VideoSortEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.model.VideoOnline;
import com.jannchie.biliob.object.VideoRankTable;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.AdminService;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jannchie.biliob.constant.SortEnum.PUBLISH_TIME;
import static com.jannchie.biliob.constant.SortEnum.VIEW_COUNT;
import static com.jannchie.biliob.constant.TimeConstant.SECOND_OF_DAY;
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
    private final VideoRepository repository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;
    private final RecommendVideo recommendVideo;
    private final AdminService adminService;
    private BiliobUtils biliOBUtils;

    @Autowired
    public VideoServiceImpl(
            VideoRepository repository,
            UserRepository userRepository,
            UserService userService,
            MongoTemplate mongoTemplate,
            RedisOps redisOps,
            RecommendVideo recommendVideo, BiliobUtils biliOBUtils, AdminService adminService) {
        this.repository = repository;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
        this.redisOps = redisOps;
        this.recommendVideo = recommendVideo;
        this.biliOBUtils = biliOBUtils;
        this.adminService = adminService;
    }

    /**
     * get popular keyword
     *
     * @return keyword list
     */
    @Override
    @Cacheable(value = "popular_keyword")
    public List getPopularTag() {
        int delta = 7;
        int compare = 90;
        VideoServiceImpl.logger.info("获取最流行的TAG列表");
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, -delta);

        Aggregation a =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("datetime").gt(c.getTime())),
                        Aggregation.project("tag", "cView", "datetime"),
                        Aggregation.unwind("tag"),
                        Aggregation.group("tag").sum("cView").as("value").count().as("count"),
                        Aggregation.match(Criteria.where("count").gt(10)),
                        Aggregation.sort(Sort.Direction.DESC, "value"),
                        Aggregation.limit(100));
        c.add(Calendar.DATE, -compare);
        Date fe = c.getTime();
        c.add(Calendar.DATE, -delta);
        Date fs = c.getTime();

        Aggregation b =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("datetime").lt(fe).gt(fs)),
                        Aggregation.project("tag", "cView", "datetime"),
                        Aggregation.unwind("tag"),
                        Aggregation.group("tag").sum("cView").as("value").count().as("count"),
                        Aggregation.match(Criteria.where("count").gt(10)),
                        Aggregation.sort(Sort.Direction.DESC, "value"),
                        Aggregation.limit(100));
        Map temp = mongoTemplate.aggregate(b, "video", Map.class).getMappedResults().stream().reduce((res, item) -> {
            res.put(item.get("_id"), item.get("value"));
            return res;
        }).get();
        List<Map> result = mongoTemplate.aggregate(a, "video", Map.class).getMappedResults();
        result.forEach(e -> {
            if (temp.get(e.get("_id")) != null) {
                e.put("value", (Integer) e.get("value") - (Integer) temp.get(e.get("_id")));
            }
        });
        return result;
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
                                .first("data")
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
        String finalUserName = biliOBUtils.getUserName();
        Map<?, ?> data = biliOBUtils.getVisitData(finalUserName, aid);
        VideoServiceImpl.logger.info("用户[{}]查询aid[{}]的详细数据", finalUserName, aid);
        mongoTemplate.insert(data, "video_visit");
    }


    @Override
    @Deprecated
    public Video getVideoDetails(Long aid, Integer type) {
        adminService.banItself("访问过时API", true);
        Video video;
        if (this.mongoTemplate.exists(Query.query(where("aid").is(aid).and("data.100").exists(true)), Video.class)) {
            video = getAggregatedData(aid);
        } else {
            video = repository.findByAid(aid);
        }
        HashMap<?, ?> rank = getVideoRank(video);
        video.setRank(rank);
        filterVideoData(video);
        video.setData(null);
        return video;
    }

    private void filterVideoData(Video video) {
        User user = UserUtils.getUser();
        if (user == null || user.getExp() < 100) {
            ArrayList<Video.Data> tempData = video.getData();
            tempData.removeIf(data -> {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DATE, -BiliobConstant.GUEST_VIEW_MAX_DAYS);
                        return data.getDatetime().before(c.getTime());
                    }
            );
            video.setData(tempData);
        }
    }

    // TODO: get Video rank
    public HashMap<String, Object> getVideoRank(Video video) {
        mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("video_rank")), HashMap.class, "rank_table");
        String[] keys = {"cCoin", "cView", "cDanmaku", "cLike", "cShare", "cFavorite", "cReply"};
        return null;
    }

    @Cacheable
    private VideoRankTable getRankTable() {
        String[] keys = {"cCoin", "cView", "cDanmaku", "cLike", "cShare", "cFavorite", "cReply"};
        long number = mongoTemplate.count(new Query(), Video.class);
        long length = number / 10;
//        for (String key : keys
//        ) {
//        }
        return null;
    }

    @Override
    public ResponseEntity<Message> postVideoByAid(Long aid) throws UserAlreadyFavoriteVideoException {
        userService.addFavoriteVideo(aid);
        if (repository.findByAid(aid) != null) {
            return new ResponseEntity<>(new Message(400, "系统已经观测了该视频"), HttpStatus.BAD_REQUEST);
        }
        VideoServiceImpl.logger.info(aid);
        repository.save(new Video(aid));
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
        Criteria criteria;
        if (aid != -1) {
            VideoServiceImpl.logger.info(aid);
            criteria = Criteria.where("aid").is(aid);
        } else if (!Objects.equals(text, "")) {
            if (InputInspection.isId(text)) {
                criteria = Criteria.where("aid").is(Long.valueOf(text));
            } else {
                VideoServiceImpl.logger.info(text);
                // get text
                String[] textArray = text.split(" ");
                if (textArray.length != 1) {
                    criteria = Criteria.where("keyword").in(Arrays.asList(textArray));
                } else {
                    criteria = Criteria.where("keyword").is(text);
                }
                sortKey = "cJannchie";
            }
        } else {
            if (days >= 0 && days <= 30) {
                VideoServiceImpl.logger.info("获取指定日期内的视频数据");
                c.add(Calendar.DATE, -days);
                Date date = c.getTime();
                return new MySlice<>(
                        repository.findAllByDatetimeGreaterThan(
                                date, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            } else {
                VideoServiceImpl.logger.info("获取全部视频数据");
                return new MySlice<>(
                        repository.findVideoBy(
                                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            }
        }
        return new MySlice<>(
                mongoTemplate.find(
                        Query.query(criteria)
                                .maxTimeMsec(10000)
                                .with(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))),
                        Video.class));
    }

    @Override
    @Cacheable(value = "author_other_video", key = "#aid + #mid + #page + #pagesize")
    public MySlice<Video> getAuthorOtherVideo(Long aid, Long mid, Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.SMALL_SIZE.getValue()) {
            pagesize = PageSizeEnum.SMALL_SIZE.getValue();
        }
        VideoServiceImpl.logger.info("获取作者其他数据");
        return new MySlice<>(
                repository.findAuthorOtherVideo(
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
                repository.findAuthorTopVideo(mid, PageRequest.of(page, pagesize, videoSort));
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

    @Override
    public void updateObserveFreq() {
        logger.info("[UPDATE] 调整视频观测频率");
        // 十万粉丝以上：正常观测
        List<Video> videoList = getVideoViewGt(100000);
        for (Video video : videoList
        ) {
            this.upsertVideoFreq(video.getAid(), SECOND_OF_DAY);
        }
        // 最多点击：高速观测
        List<Map> mostVisitAuthorList = this.listMostVisitVideoId(1);
        for (Map data : mostVisitAuthorList
        ) {
            this.upsertVideoFreq((Long) data.get("aid"), SECOND_OF_DAY);
        }
        logger.info("[FINISH] 调整观测频率");
    }

    private void upsertVideoFreq(Long aid, Integer interval) {
        Calendar nextCal = Calendar.getInstance();
        nextCal.add(Calendar.SECOND, interval);
        Date cTime = Calendar.getInstance().getTime();
        logger.debug("[UPSERT] 视频：{} 访问频率：{} 更新时间：{}", aid, interval, cTime);
        Update u = Update.update("date", cTime)
                .set("interval", interval);
        u.setOnInsert("next", nextCal.getTime());
        mongoTemplate.upsert(Query.query(Criteria.where("aid").is(aid)), u, "video_interval");
    }


    private List<Video> getVideoViewGt(int gt) {
        Query q = Query.query(Criteria.where("cView").gt(gt));
        q.fields().include("mid");
        return mongoTemplate.find(q, Video.class, "video");
    }

    private List<Map> listMostVisitVideoId(Integer days, Integer limit) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("date").gt(c)),
                        Aggregation.group("mid").count().as("count"),
                        Aggregation.sort(Sort.Direction.DESC, "count"),
                        Aggregation.limit(limit)), "video_visit", Map.class).getMappedResults();
    }

    private List<Map> listMostVisitVideoId(Integer days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("date").gt(c)),
                        Aggregation.group("mid").count().as("count"),
                        Aggregation.sort(Sort.Direction.DESC, "count")), "video_visit", Map.class).getMappedResults();
    }
}
