package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.TestConstants;
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
public class MonitorControllerTest {
    @Autowired
    MonitorController monitorController;

    @Test
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void getVideoInfo() {
        monitorController.getCrawlRateAuthor();
    }
}