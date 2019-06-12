package com.jannchie.biliob.utils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/** @author jannchie */
@Component
public class RedisOps {
  private static final String VIDEO_URL = "https://api.bilibili.com/x/article/archives?ids=%d&%s";
  private static final String VIDEO_KEY = "videoRedis:start_urls";
  private static final String AUTHOR_URL =
      "https://api.bilibili.com/x/web-interface/card?mid=%d&%s";
  private static final String AUTHOR_KEY = "authorRedis:start_urls";
  private static final String DANMAKU_FROM_AID_URL =
      "https://api.bilibili.com/x/web-interface/view?aid=%d&%s";
  private static final String DANMAKU_KEY = "DanmakuAggregate:start_urls";
  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public RedisOps(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Long getAuthorQueueLength() {
    try {
      return redisTemplate.opsForList().size(RedisOps.AUTHOR_KEY);
    } catch (Exception e) {
      // occurred error when getting the length of author crawl tasks queue
      return null;
    }
  }

  public Long getVideoQueueLength() {
    try {
      return redisTemplate.opsForList().size(RedisOps.VIDEO_KEY);
    } catch (Exception e) {
      // occurred error when getting the length of author crawl tasks queue
      return null;
    }
  }

  private void sentRequest(String key, String value) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
    } catch (Exception e) {
      redisTemplate.opsForList().rightPush(key, value);
    }
  }

  public void postVideoCrawlTask(Long aid) {
    String url = String.format(RedisOps.VIDEO_URL, aid, null);
    sentRequest(RedisOps.VIDEO_KEY, url);
  }

  public void postVideoCrawlTask(String aid) {
    String url = String.format(RedisOps.VIDEO_URL, Long.valueOf(aid), null);
    sentRequest(RedisOps.VIDEO_KEY, url);
  }

  public void postVideoCrawlTask(Long aid, ObjectId objectId) {
    String url = String.format(RedisOps.VIDEO_URL, aid, objectId);
    sentRequest(RedisOps.VIDEO_KEY, url);
  }

  public void postAuthorCrawlTask(Long mid, ObjectId objectId) {
    String url = String.format(RedisOps.AUTHOR_URL, mid, objectId);
    sentRequest(RedisOps.AUTHOR_KEY, url);
  }

  public void postAuthorCrawlTask(Long mid) {
    String url = String.format(RedisOps.AUTHOR_URL, mid, null);
    sentRequest(RedisOps.AUTHOR_KEY, url);
  }

  public void postAuthorCrawlTask(String mid) {
    String url = String.format(RedisOps.AUTHOR_URL, Long.valueOf(mid), null);
    sentRequest(RedisOps.AUTHOR_KEY, url);
  }

  public void postDanmakuAggregateTask(Long aid, ObjectId objectId) {
    String url = String.format(RedisOps.DANMAKU_FROM_AID_URL, aid, objectId);
    sentRequest(RedisOps.DANMAKU_KEY, url);
  }
}
