package com.jannchie.biliob.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DataReducerTest {

  @Autowired private DataReducer dataReducer;

  @Test
  @Transactional
  public void limitPagesize() throws Exception {
    Assert.assertEquals("输入0页", (Integer) 0, DataReducer.limitPagesize(0));
    Assert.assertEquals("输入30页", (Integer) 0, DataReducer.limitPagesize(30));
    Assert.assertEquals("输入-1页", (Integer) 0, DataReducer.limitPagesize(-1));
    Assert.assertEquals("输入20页", (Integer) 20, DataReducer.limitPagesize(20));
    Assert.assertEquals("输入1页", (Integer) 1, DataReducer.limitPagesize(1));
  }
}
