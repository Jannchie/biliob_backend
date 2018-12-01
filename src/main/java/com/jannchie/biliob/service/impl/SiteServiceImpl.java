package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.Site;
import com.jannchie.biliob.service.SiteService;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.ResultEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


/**
 * @author jannchie
 */
@Service
public class SiteServiceImpl implements SiteService {

  private static final  Integer MAX_ONLINE_PLAY_RANGE = 7;
  private static final Integer HOUR_IN_DAY = 24;

  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private final MongoTemplate mongoTemplate;

  @Autowired
  public SiteServiceImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * Get the data of the number of people watching video on bilibili.
   *
   * @param days The days of data that this method should return.
   * @return Online number result.
   */
  @Override
  public Result listOnline(Integer days) {
    if (days > MAX_ONLINE_PLAY_RANGE) {
      return new Result(ResultEnum.OUT_OF_RANGE);
    }
    Integer limit = days * HOUR_IN_DAY;
    Query query = new Query();
    query.limit(limit).with(new Sort(Sort.Direction.DESC, "datetime"));
    logger.info("获得全站在线播放数据");
    return new Result(ResultEnum.SUCCEED,mongoTemplate.find(query, Site.class, "site_info"));
  }
}
