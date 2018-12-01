package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.exception.BusinessException;
import com.jannchie.biliob.model.Site;
import com.jannchie.biliob.service.SiteService;
import com.jannchie.biliob.utils.ExceptionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jannchie.biliob.constant.Common.HOUR_IN_DAY;
import static com.jannchie.biliob.constant.Common.MAX_ONLINE_PLAY_RANGE;

/**
 * @author jannchie
 */
@Service
public class SiteServiceImpl implements SiteService {

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
   * @return Online number.
   */
  @Override
  public List<Site> getPlayOnline(Integer days) {
    if (days > MAX_ONLINE_PLAY_RANGE) {
      throw new BusinessException(ExceptionMessage.OUT_OF_RANGE);
    }
    Integer limit = days * HOUR_IN_DAY;
    Query query = new Query();
    query.limit(limit).with(new Sort(Sort.Direction.DESC, "datetime"));
    logger.info("获得全站在线播放数据");
    return mongoTemplate.find(query, Site.class, "site_info");
  }
}
