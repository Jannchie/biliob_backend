package com.jannchie.biliob.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TracerSchedulerTest {
  @Autowired MongoTemplate mongoTemplate;
  @Autowired private TracerScheduler t = new TracerScheduler(mongoTemplate);

  @Test
  @Transactional
  public void checkDeadTask() throws Exception {
    Assert.assertEquals("获得返回值", t.getDeadDate().getClass(), Date.class);
  }
}
