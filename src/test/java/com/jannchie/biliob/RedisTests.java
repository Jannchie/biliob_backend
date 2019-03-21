package com.jannchie.biliob;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RedisTests {

  private String userKey = "userKey";

  @Autowired private RedisTemplate redisTemplate;
  @Autowired private StringRedisTemplate stringRedisTemplate;

  @Test
  public void contextLoads() {}

  @Test
  @Transactional
  public void testURL() {
    String testString = "https://api.bilibili.com/x/web-interface/card?mid=1850091";
    stringRedisTemplate
        .opsForList()
        .leftPush(
            "authorRedis:start_urls", "https://api.bilibili.com/x/web-interface/card?mid=1850091");
    String s = stringRedisTemplate.opsForList().leftPop("authorRedis:start_urls");
    Assert.assertEquals("URL添加与删除测试失败", testString, s);
  }
}
