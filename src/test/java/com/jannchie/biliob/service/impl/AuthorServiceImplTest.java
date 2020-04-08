package com.jannchie.biliob.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AuthorServiceImplTest {
    @Autowired
    AuthorServiceImpl authorService;

    @Test
    public void upsertAuthorFreq() {
        authorService.upsertAuthorFreq(1850091L, 20);
    }

    @Test
    public void getAggregatedData() throws Exception {
    }

    @Test
    public void getAuthorDetails() throws Exception {
    }

    @Test
    public void postAuthorByMid() throws Exception {
    }

    @Test
    public void getAuthor() throws Exception {
    }

    @Test
    public void listFansIncreaseRate() throws Exception {
    }

    @Test
    public void listFansDecreaseRate() throws Exception {
    }

    @Test
    public void listFansRate() throws Exception {
    }

    @Test
    public void getAuthorInfo() throws Exception {
    }

    @Test
    public void getRealTimeData() throws Exception {
    }
}
