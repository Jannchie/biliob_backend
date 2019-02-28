package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.model.VideoOnline;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.InputInspection;
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.MySlice;
import com.jannchie.biliob.utils.RedisOps;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.jannchie.biliob.constant.SortEnum.PUBLISH_TIME;
import static com.jannchie.biliob.constant.SortEnum.VIEW_COUNT;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/** @author jannchie */
@Service
@CacheConfig(cacheNames = "videoService")
public class VideoServiceImpl implements VideoService {
  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private static final Integer MAX_PAGE_SIZE = 10;
  private final RedisOps redisOps;
  private final VideoRepository respository;
  private final UserService userService;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public VideoServiceImpl(
      VideoRepository respository,
      UserRepository userRepository,
      UserService userService,
      MongoTemplate mongoTemplate,
      RedisOps redisOps) {
    this.respository = respository;
    this.userService = userService;
    this.mongoTemplate = mongoTemplate;
    this.redisOps = redisOps;
  }

  @Override
  @Cacheable(value = "video_detail", key = "#aid")
  public Video getVideoDetails(Long aid) {
    VideoServiceImpl.logger.info(aid);
    return respository.findByAid(aid);
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
  @Cacheable(value = "video_slice", key = "#aid + #text + #page + #pagesize")
  public MySlice<Video> getVideo(Long aid, String text, Integer page, Integer pagesize) {
    if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
      pagesize = PageSizeEnum.BIG_SIZE.getValue();
    }
    if (!(aid == -1)) {
      VideoServiceImpl.logger.info(aid);
      return new MySlice<>(
          respository.searchByAid(
              aid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cView"))));
    } else if (!Objects.equals(text, "")) {
      if (InputInspection.isId(text)) {
        return new MySlice<>(
            respository.searchByAid(
                Long.valueOf(text),
                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cView"))));
      }
      VideoServiceImpl.logger.info(text);
      // get text
      String[] textArray = text.split(" ");
      MySlice<Video> mySlice = new MySlice<>(
          respository.findByKeywordContaining(
              textArray, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cView"))));
      if (mySlice.getContent().isEmpty()) {
        for (String eachText : textArray) {
          HashMap<String, String> map = new HashMap<>(1);
          map.put("aid", eachText);
          mongoTemplate.insert(map, "search_word");
        }
      }
      return mySlice;
    } else {
      VideoServiceImpl.logger.info("获取全部视频数据");
      return new MySlice<>(
          respository.findAllByDataIsNotNull(
              PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cView"))));
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
   * @param mid author id
   * @param page no use
   * @param pagesize the number of displayed video
   * @param sort 0: order by view || 1: order by publish datetime.
   * @return slice of author's video || null: param error
   */
  @Override
  @Cacheable(value = "author_top_video", key = "#mid + #page + #pagesize + #sort")
  public MySlice<Video> getAuthorTopVideo(Long mid, Integer page, Integer pagesize, Integer sort) {
    if (pagesize >= VideoServiceImpl.MAX_PAGE_SIZE) {
      return null;
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
}
