package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.TestConstants;
import com.jannchie.biliob.model.VideoInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class VideoControllerV3Test {

    @Autowired
    VideoControllerV3 videoControllerV3;

    @Test
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void getVideoInfo() {
        VideoInfo a = videoControllerV3.getVideoInfo(170001L);
        VideoInfo b = videoControllerV3.getVideoInfo("17x411w7KC");
        Assert.assertEquals(a.getAid(), b.getAid());
        Assert.assertEquals(a.getBvid(), b.getBvid());
    }


    @Test
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void listVideoStatsByAid() {
        videoControllerV3.listVideoStats(170001L);
        videoControllerV3.listVideoStats("17x411w7KC");
    }
}