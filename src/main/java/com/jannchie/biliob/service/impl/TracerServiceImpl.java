package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.service.TracerService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/** @author jannchie */
@Service
public class TracerServiceImpl implements TracerService {

  private static final Integer MAX_ONLINE_PLAY_RANGE = 30;
  private static final Integer HOUR_IN_DAY = 24;

  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private final MongoTemplate mongoTemplate;
  private final RedisOps redisOps;

  @Autowired
  public TracerServiceImpl(MongoTemplate mongoTemplate, RedisOps redisOps) {
    this.mongoTemplate = mongoTemplate;
    this.redisOps = redisOps;
  }

  /**
   * It is the function to get authors' queue status.
   *
   * @return The authors' queue status.
   */
  @Override
  public ResponseEntity getAuthorQueueStatus() {
    Map<String, Long> result = new HashMap<>(1);
    Long authorCrawlTasksQueueLength = redisOps.getAuthorQueueLength();
    result.put("length", authorCrawlTasksQueueLength);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * It is the function to get videos' queue status.
   *
   * @return The videos' queue status.
   */
  @Override
  public ResponseEntity getVideoQueueStatus() {
    Map<String, Long> result = new HashMap<>(1);
    Long videoCrawlTasksQueueLength = redisOps.getVideoQueueLength();
    result.put("length", videoCrawlTasksQueueLength);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
