package com.jannchie.biliob.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailUtilTest {
    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void sendUserActiveCode() throws Exception {
    }

    @Test
    public void sendActiveCode() throws Exception {
    }
}
