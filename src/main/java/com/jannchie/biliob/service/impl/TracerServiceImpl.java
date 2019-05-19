package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.TaskStatusEnum;
import com.jannchie.biliob.repository.TracerRepository;
import com.jannchie.biliob.service.TracerService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.jannchie.biliob.constant.TaskTypeEnum.GET_ALL;
import static com.jannchie.biliob.constant.TaskTypeEnum.GET_RUNNING;

/** @author jannchie */
@Service
public class TracerServiceImpl implements TracerService {

  private static final Integer MAX_ONLINE_PLAY_RANGE = 30;
  private static final Integer HOUR_IN_DAY = 24;

  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private final MongoTemplate mongoTemplate;
  private final TracerRepository tracerRepository;
  private final RedisOps redisOps;

  @Autowired
  public TracerServiceImpl(
      MongoTemplate mongoTemplate, TracerRepository tracerRepository, RedisOps redisOps) {
    this.mongoTemplate = mongoTemplate;
    this.tracerRepository = tracerRepository;
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

  /**
   * Get the slice of exists task of the system.
   *
   * <p>It is able to get the status of Biliob scheduler and Biliob spider.
   *
   * @param page The page number of the task slice.
   * @param pagesize The page size of the task slice.
   * @return tTe slice of exists task of the system.
   */
  @Override
  public ResponseEntity sliceExistsTask(Integer page, Integer pagesize) {
    return new ResponseEntity<>(
        tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
            "ExistsTask", PageRequest.of(page, pagesize)),
        HttpStatus.OK);
  }

  /**
   * Get the slice of progress task of the system.
   *
   * <p>It is able to get the status of Biliob link generate task.
   *
   * @param page The page number of the task slice.
   * @param pagesize The page size of the task slice.
   * @return tTe slice of exists task of the system.
   */
  @Override
  public ResponseEntity sliceProgressTask(Integer page, Integer pagesize) {
    return new ResponseEntity<>(
        tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
            "ProgressTask", PageRequest.of(page, pagesize)),
        HttpStatus.OK);
  }

  @Override
  public ResponseEntity sliceSpiderTask(Integer page, Integer pagesize, Integer type) {
    if (type.equals(GET_ALL.value)) {
      return new ResponseEntity<>(
          tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
              "SpiderTask", PageRequest.of(page, pagesize)),
          HttpStatus.OK);
    } else if (type.equals(GET_RUNNING.value)) {
      return new ResponseEntity<>(
          tracerRepository.findTracerByClassNameAndStatusOrderByUpdateTimeDesc(
              "SpiderTask", TaskStatusEnum.ALIVE.value, PageRequest.of(page, pagesize)),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(
        tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
            "SpiderTask", PageRequest.of(page, pagesize)),
        HttpStatus.OK);
  }
}
