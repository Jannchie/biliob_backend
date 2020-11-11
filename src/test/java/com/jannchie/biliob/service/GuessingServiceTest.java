package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.TestConstants;
import com.jannchie.biliob.model.FansGuessingItem;
import com.jannchie.biliob.model.GuessingItem;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class GuessingServiceTest {

    @Autowired
    GuessingService guessingService;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private UserUtils userUtils;

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void joinFinished() {
        GuessingItem.PokerChip gp = new GuessingItem.PokerChip();
        gp.setCredit(12D);
        Calendar c = Calendar.getInstance();
        gp.setCreateTime(c.getTime());
        gp.setGuessingDate(c.getTime());
        Result<?> result = guessingService.joinFansGuessing("5e84bbc0b2dfc1a238c8ec9e", gp);
        Assert.assertEquals(result.getMsg(), ResultEnum.EXECUTE_FAILURE.getMsg());
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void join() {
        FansGuessingItem f = mongoTemplate.findOne(Query.query(Criteria.where("state").is(1)), FansGuessingItem.class);
        assert f != null;
        GuessingItem.PokerChip gp = new GuessingItem.PokerChip();
        gp.setCredit(16.2D);
        Calendar c = Calendar.getInstance();
        gp.setCreateTime(c.getTime());
        c.add(Calendar.HOUR, 100);
        gp.setGuessingDate(c.getTime());
        Result<?> result = guessingService.joinFansGuessing(f.getGuessingId(), gp);
        Assert.assertEquals(result.getMsg(), ResultEnum.SUCCEED.getMsg());
        User u = userUtils.getUser();
        Assert.assertEquals(u.getCredit(), result.getUser().getCredit());
    }

    @Test
    public void printGuessingResult() {
        Calendar c = Calendar.getInstance();
        guessingService.printGuessingResult("5e84bbc0b2dfc1a238c8ec9e");
    }
}