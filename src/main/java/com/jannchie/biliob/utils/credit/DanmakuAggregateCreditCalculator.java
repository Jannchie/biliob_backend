package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.utils.RedisOps;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** @author jannchie */
@Component
@Transactional(rollbackFor = Exception.class)
public class DanmakuAggregateCreditCalculator extends AbstractCreditCalculator {

  private final RedisOps redisOps;

  @Autowired
  public DanmakuAggregateCreditCalculator(MongoTemplate mongoTemplate, RedisOps redisOps) {
    super(mongoTemplate);
    this.redisOps = redisOps;
  }

  /**
   * Execute the service
   *
   * @param id just id param
   * @return Whether the service executed correctly.
   */
  @Override
  void execute(Long id, ObjectId objectId) {
    redisOps.postDanmakuAggregateTask(id, objectId);
  }
}
