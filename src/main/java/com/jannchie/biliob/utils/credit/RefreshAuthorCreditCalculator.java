package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.utils.RedisOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class RefreshAuthorCreditCalculator extends AbstractCreditCalculator {

  private final RedisOps redisOps;

  @Autowired
  public RefreshAuthorCreditCalculator(MongoOperations mongoTemplate, RedisOps redisOps) {
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
    Long mid = (Long) data;
    redisOps.postAuthorCrawlTask(mid);
  }

  /**
   * Execute the service
   *
   * @param id just id param
   */
  @Override
  void execute(Long id) {
    redisOps.postAuthorCrawlTask(id);
  }
}
