package com.jannchie.biliob;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisTests extends BiliobApplicationTests {

  private String userKey = "userKey";

  @Autowired
  private RedisTemplate redisTemplate;
  @Autowired
  private StringRedisTemplate stringRedisTemplate;


  @Test
  public void contextLoads() {
  }

  @Test
  public void testURL() {
    String testString = "https://api.bilibili.com/x/web-interface/card?mid=1850091";
    stringRedisTemplate.opsForList().leftPush("authorRedis:start_urls", "https://api.bilibili.com/x/web-interface/card?mid=1850091");
    String s = stringRedisTemplate.opsForList().leftPop("authorRedis:start_urls");
    Assert.assertEquals("URL添加与删除测试失败", testString, s);
  }
}
