package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author jannchie
 */
@Component
public class RefreshAuthorCreditCalculator extends AbstractCreditCalculator {


  private static final String URL = "https://api.bilibili.com/x/web-interface/card?mid=%d";
  private static final String KEY = "authorRedis:start_urls";
  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public RefreshAuthorCreditCalculator(MongoOperations mongoTemplate, UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
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
    Integer mid = (Integer) data;
    String url = String.format(URL, mid);

    redisTemplate.opsForList().rightPush(KEY, url);
  }
}
