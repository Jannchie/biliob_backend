package com.jannchie.biliob.utils.schedule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SpiderSchedulerTest {

    @Autowired
    private SpiderScheduler spiderScheduler;

    @Test
    public void updateAuthorData() {
        spiderScheduler.updateAuthorData();
    }

    @Test
    public void updateVideoData() {
    }

    @Test
    public void updateEvent() {
    }

    @Test
    public void addAuthor() {
    }

    @Test
    public void addAuthorLatestVideo() {
    }

    @Test
    public void updateObserveFreq() {
        spiderScheduler.updateAuthorFreq();
    }

    @Test
    public void addOnlineTopVideo() {

    }

    @Test
    public void updateSiteInfo() {
    }
}