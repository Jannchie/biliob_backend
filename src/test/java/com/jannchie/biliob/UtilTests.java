package com.jannchie.biliob;

import com.jannchie.biliob.utils.InputInspection;
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
public class UtilTests {

  private String userKey = "userKey";

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @Test
  @Transactional
  public void testInputInspection() {
    Assert.assertEquals("正常输入", InputInspection.isId("123"), true);
    Assert.assertEquals("ID太长", InputInspection.isId("124415361324123212"), false);
    Assert.assertEquals("ID为负数", InputInspection.isId("-1"), false);
    Assert.assertEquals("ID为0", InputInspection.isId("0"), false);
    Assert.assertEquals("ID尾部包含字符", InputInspection.isId("1231a"), false);
    Assert.assertEquals("ID中间包含字符", InputInspection.isId("31b1"), false);
    Assert.assertEquals("ID头部包含字符", InputInspection.isId("a1"), false);
    Assert.assertEquals("ID为空", InputInspection.isId(""), false);
    Assert.assertEquals("ID为null", InputInspection.isId(null), false);
  }
}
