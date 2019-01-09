package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.jannchie.biliob.constant.SortEnum.PUBLISH_TIME;
import static com.jannchie.biliob.constant.SortEnum.VIEW_COUNT;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Service
public class VideoServiceImpl implements VideoService {
	private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
	private final VideoRepository respository;
	private final UserService userService;
  private final MongoTemplate mongoTemplate;
  private static final Integer MAX_PAGE_SIZE = 10;

  @Autowired
  public VideoServiceImpl(
      VideoRepository respository, UserRepository userRepository, UserService userService, MongoTemplate mongoTemplate) {
    this.respository = respository;
    this.userService = userService;
    this.mongoTemplate = mongoTemplate;
  }

	@Override
	public Video getVideoDetails(Long aid) {
		logger.info(aid);
		return respository.findByAid(aid);
	}

	@Override
	public ResponseEntity<Message> postVideoByAid(Long aid)
			throws UserAlreadyFavoriteVideoException {
    userService.addFavoriteVideo(aid);
    if (respository.findByAid(aid) != null) {
      return new ResponseEntity<>(new Message(400, "系统已经观测了该视频"), HttpStatus.BAD_REQUEST);
		}
		logger.info(aid);
		respository.save(new Video(aid));
		return new ResponseEntity<>(new Message(200, "观测视频成功"), HttpStatus.OK);
	}

	@Override
	public Page<Video> getVideo(Long aid, String text, Integer page, Integer pagesize) {
    if(pagesize > PageSizeEnum.BIG_SIZE.getValue()){
      pagesize = PageSizeEnum.BIG_SIZE.getValue();
    }
		if (!(aid == -1)) {
			logger.info(aid);
			return respository.searchByAid(
					aid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
		} else if (!Objects.equals(text, "")) {
			logger.info(text);
			return respository.searchByText(
					text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
		} else {
			logger.info("获取全部视频数据");
			return respository.findByDataIsNotNull(
					PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
		}
	}

	@Override
	public Slice<Video> getAuthorOtherVideo(Long aid, Long mid, Integer page, Integer pagesize) {
    if(pagesize > PageSizeEnum.SMALL_SIZE.getValue()){
      pagesize = PageSizeEnum.SMALL_SIZE.getValue();
    }
    logger.info("获取作者其他数据");
    return respository.findAuthorOtherVideo(
        aid, mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
	}

  /**
   * Get author top video.
   *
   * @param mid      author id
   * @param page     no use
   * @param pagesize the number of displayed video
   * @param sort 0: order by view || 1: order by publish datetime.
   * @return slice of author's video
   */
  @Override
  public ResponseEntity getAuthorTopVideo(Long mid, Integer page, Integer pagesize, Integer sort) {
    if (pagesize >= MAX_PAGE_SIZE) {
      return new ResponseEntity<>(new Result(ResultEnum.PARAM_ERROR), HttpStatus.BAD_REQUEST);
    }
    Sort videoSort;
    if (Objects.equals(sort, VIEW_COUNT.getValue())) {
      videoSort = new Sort(Sort.Direction.DESC, "data.0.view");
    } else if (Objects.equals(sort, PUBLISH_TIME.getValue())) {
      videoSort = new Sort(Sort.Direction.DESC, "datetime");
    } else {
      return new ResponseEntity<>(new Result(ResultEnum.PARAM_ERROR), HttpStatus.BAD_REQUEST);
    }

    Slice video = respository.findAuthorTopVideo(
        mid, PageRequest.of(page, pagesize, videoSort));
    logger.info("获取mid:{} 播放最多的视频", mid);
    return new ResponseEntity<>(video, HttpStatus.OK);
  }

  /**
   * Get my video.
   *
   * @return the latest of my video
   */
  @Override
  public ResponseEntity<Video> getMyVideo() {
    Query q = new Query(where("mid").is(1850091)).with(new Sort(Sort.Direction.DESC, "datetime"));
    q.fields().exclude("data");
    Video video = mongoTemplate.findOne(q, Video.class);
    logger.info("获取广告");
    return new ResponseEntity<>(video, HttpStatus.OK);
  }
}
