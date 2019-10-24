package com.jannchie.biliob.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DataReducerTest {

    @Autowired
    private DataReducer dataReducer;

    @Test
    public void limitPagesize() throws Exception {

    }
}
