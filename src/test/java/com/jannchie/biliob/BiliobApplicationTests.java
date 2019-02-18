package com.jannchie.biliob;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Suite.class)
@SpringBootTest
@WebAppConfiguration
@Suite.SuiteClasses({RedisTests.class, UtilTests.class})
public class BiliobApplicationTests {
}
