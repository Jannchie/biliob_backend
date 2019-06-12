package com.jannchie.biliob;

import com.jannchie.biliob.service.impl.AuthorServiceImplTest;
import com.jannchie.biliob.service.impl.UserServiceImplTest;
import com.jannchie.biliob.service.impl.VideoServiceImplTest;
import com.jannchie.biliob.utils.DataReducerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Suite.class)
@SpringBootTest
@WebAppConfiguration
@Suite.SuiteClasses({
    RedisTests.class,
    UtilTests.class,
    DataReducerTest.class,
    AuthorServiceImplTest.class,
    VideoServiceImplTest.class,
    UserServiceImplTest.class
})
public class BiliobApplicationTests {}
