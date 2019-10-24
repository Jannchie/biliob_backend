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

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UtilTests {

    private String userKey = "userKey";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testInputInspection() {
        Assert.assertTrue("正常输入", InputInspection.isId("123"));
        Assert.assertFalse("ID太长", InputInspection.isId("124415361324123212"));
        Assert.assertFalse("ID为负数", InputInspection.isId("-1"));
        Assert.assertFalse("ID为0", InputInspection.isId("0"));
        Assert.assertFalse("ID尾部包含字符", InputInspection.isId("1231a"));
        Assert.assertFalse("ID中间包含字符", InputInspection.isId("31b1"));
        Assert.assertFalse("ID头部包含字符", InputInspection.isId("a1"));
        Assert.assertFalse("ID为空", InputInspection.isId(""));
        Assert.assertFalse("ID为null", InputInspection.isId(null));
    }
}
