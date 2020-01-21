package com.jannchie.biliob;

import com.jannchie.biliob.utils.RedisOps;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RedisTests {

    @Autowired
    private RedisOps redisOps;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getAuthorQueueLength() {
        Long authorCrawlTaskQueueLength = redisOps.getAuthorQueueLength();
        Assert.assertEquals("返回值非Long类型", "class java.lang.Long", authorCrawlTaskQueueLength.getClass().toString());
    }
}
