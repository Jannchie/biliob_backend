package com.jannchie.biliob;

import com.jannchie.biliob.service.VideoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VideoTests extends BiliobApplicationTests {
  @Autowired
  private VideoService videoService;

  @Test
  public void testGetVideo() {
  }
}
