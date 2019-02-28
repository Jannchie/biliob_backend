package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.utils.RedisOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class RefreshVideoCreditCalculator extends AbstractCreditCalculator {

  private final RedisOps redisOps;

  @Autowired
  public RefreshVideoCreditCalculator(MongoOperations mongoTemplate, RedisOps redisOps) {
    super(mongoTemplate);
    this.redisOps = redisOps;
  }

  /**
   * Execute the service
   *
   * @param data just param
   * @return Whether the service executed correctly.
   */
  @Override
  void execute(Object data) {
    Long aid = (Long) data;
    redisOps.postVideoCrawlTask(aid);
  }

  /**
   * Execute the service
   *
   * @param id just id param
   */
  @Override
  void execute(Long id) {
    redisOps.postVideoCrawlTask(id);
  }
}
