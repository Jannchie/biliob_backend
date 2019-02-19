package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class RefreshVideoCreditCalculator extends AbstractCreditCalculator {

  private static final String URL = "https://api.bilibili.com/x/article/archives?ids=%d";
  private static final String KEY = "videoRedis:start_urls";
  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public RefreshVideoCreditCalculator(
      MongoOperations mongoTemplate,
      UserRepository userRepository,
      RedisTemplate<String, String> redisTemplate) {
    super(mongoTemplate, userRepository);
    this.redisTemplate = redisTemplate;
  }

  /**
   * Execute the service
   *
   * @param data just param
   * @return Whether the service executed correctly.
   */
  @Override
  void execute(Object data) {
    Integer aid = (Integer) data;
    String url = String.format(RefreshVideoCreditCalculator.URL, aid);
    redisTemplate.opsForList().rightPush(RefreshVideoCreditCalculator.KEY, url);
  }
}
