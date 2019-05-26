package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.service.VideoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class VideoServiceImplTest {
  @Autowired VideoService videoService;

  @Test
  public void getAggregatedData() throws Exception {
    videoService.getAggregatedData(18089528L);
  }

  @Test
  public void getVideoDetails() throws Exception {}

  @Test
  public void postVideoByAid() throws Exception {}

  @Test
  public void getVideo() throws Exception {}

  @Test
  public void getAuthorOtherVideo() throws Exception {}

  @Test
  public void getAuthorTopVideo() throws Exception {}

  @Test
  public void getMyVideo() throws Exception {}

  @Test
  public void listOnlineVideo() throws Exception {}

  @Test
  public void getNumberOfVideo() throws Exception {}

  @Test
  public void getRankTable() throws Exception {}
}
