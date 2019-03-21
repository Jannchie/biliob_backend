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
public class RefreshAuthorCreditCalculator extends AbstractCreditCalculator {

  private final RedisOps redisOps;

  @Autowired
  public RefreshAuthorCreditCalculator(MongoTemplate mongoTemplate, RedisOps redisOps) {
    super(mongoTemplate);
    this.redisOps = redisOps;
  }

  @Override
  void execute(Long id, ObjectId objectId) {
    redisOps.postAuthorCrawlTask(id, objectId);
  }
}
