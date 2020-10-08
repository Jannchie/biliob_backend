package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.utils.MySlice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @Test
    public void getAuthorOtherVideo() {
        MySlice<Video> videoSlice = videoService.getAuthorOtherVideo(62428913L, 1850091L, 0, 20);
        Assert.assertNotNull(videoSlice);
    }

    @Test
    public void getAuthorTopVideo() {
        MySlice<Video> videoSlice1 = videoService.getAuthorTopVideo(1850091L, 0, 5, 1);
        MySlice<Video> videoSlice2 = videoService.getAuthorTopVideo(1850091L, 0, 5, 0);
        Assert.assertNotNull(videoSlice1);
        Assert.assertNotNull(videoSlice2);
    }
}