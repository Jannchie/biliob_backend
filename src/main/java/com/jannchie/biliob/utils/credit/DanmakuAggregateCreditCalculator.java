package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.utils.RedisOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class DanmakuAggregateCreditCalculator extends AbstractCreditCalculator {

  private final RedisOps redisOps;

  @Autowired
  public DanmakuAggregateCreditCalculator(MongoOperations mongoTemplate, RedisOps redisOps) {
    super(mongoTemplate);
    this.redisOps = redisOps;
  }

  /**
   * Execute the service
   *
   * @param data just param
   */
  @Override
  void execute(Object data) {
    redisOps.postDanmakuAggregateTask((Long) data);
  }

  /**
   * Execute the service
   *
   * @param id just id param
   * @return Whether the service executed correctly.
   */
  @Override
  void execute(Long id) {
    redisOps.postDanmakuAggregateTask(id);
  }
}
