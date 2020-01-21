package com.jannchie.biliob;

import com.jannchie.biliob.service.SiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SiteInfoTests {

    @Autowired
    SiteService siteService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testListOnline() {
    }
}
