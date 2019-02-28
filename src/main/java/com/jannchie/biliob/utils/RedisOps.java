package com.jannchie.biliob.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class RedisOps {
  private static final String VIDEO_URL = "https://api.bilibili.com/x/article/archives?ids=%d";
  private static final String VIDEO_KEY = "videoRedis:start_urls";
  private static final String AUTHOR_URL = "https://api.bilibili.com/x/web-interface/card?mid=%d";
  private static final String AUTHOR_KEY = "authorRedis:start_urls";
  private static final String DANMAKU_FROM_AID_URL =
      "https://api.bilibili.com/x/web-interface/view?aid=%d";
  private static final String DANMAKU_KEY = "DanmakuAggregate:start_urls";
  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public RedisOps(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void postVideoCrawlTask(Long aid) {
    String url = String.format(RedisOps.VIDEO_URL, aid);
    redisTemplate.opsForList().rightPush(RedisOps.VIDEO_KEY, url);
  }

  public void postAuthorCrawlTask(Long mid) {
    String url = String.format(RedisOps.AUTHOR_URL, mid);
    redisTemplate.opsForList().rightPush(RedisOps.AUTHOR_KEY, url);
  }

  public void postDanmakuAggregateTask(Long aid) {
    String url = String.format(RedisOps.DANMAKU_FROM_AID_URL, aid);
    redisTemplate.opsForList().rightPush(RedisOps.DANMAKU_KEY, url);
  }
}
