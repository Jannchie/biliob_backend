package com.jannchie.biliob.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class GuessingServiceTest {

    @Autowired
    GuessingService guessingService;


    @Test
    public void printGuessingResult() {
        Calendar c = Calendar.getInstance();
        guessingService.printGuessingResult("5e84bbc0b2dfc1a238c8ec9e");
    }
}