package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.TestConstants;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;


    @Test
    @Transactional
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void testPostCheckIn() {
        Result<?> r = userService.postCheckIn();
        assert r.getMsg().equals(ResultEnum.SUCCEED.getMsg());
        r = userService.postCheckIn();
        assert r.getMsg().equals(ResultEnum.ALREADY_SIGNED.getMsg());
    }

    @Test
    @Transactional
    public void testPostCheckInWithOutLogin() {
        Result<?> r = userService.postCheckIn();
        assert r.getMsg().equals(ResultEnum.HAS_NOT_LOGGED_IN.getMsg());
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.TEST_USER_NAME)
    public void testForceFocus() {
        Result<?> r;
        User u = UserUtils.getUser();
        assert u != null;
        Double credit = u.getCredit();
        r = userService.forceFocus(1850091L, true);
        Assert.assertEquals(r.getMsg(), ResultEnum.ALREADY_FORCE_FOCUS.getMsg());
        r = userService.forceFocus(-404L, true);
        Assert.assertEquals(r.getMsg(), ResultEnum.AUTHOR_NOT_FOUND.getMsg());
        r = userService.forceFocus(58402261L, true);
        Assert.assertEquals(r.getMsg(), ResultEnum.SUCCEED.getMsg());
        u = UserUtils.getUser();
        assert u != null;
        Result.UserData ud = r.getUser();
        assert ud != null;
        Double d = u.getCredit() + CreditConstant.SET_AUTHOR_FORCE_OBSERVE.getValue();
        Assert.assertEquals(d, ud.getCredit());
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void testCreditNotEnough() {
        Result<?> r = userService.forceFocus(58402261L, true);
        Assert.assertEquals(r.getMsg(), ResultEnum.CREDIT_NOT_ENOUGH.getMsg());
    }

    @Test
    public void testPostQuestion() {
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void testRefreshAuthor() {
        Result<?> r = userService.refreshAuthor(1850091L);
        Assert.assertEquals(r.getMsg(), ResultEnum.ACCEPTED.getMsg());
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void testRefreshVideo() {
        Result<?> r = userService.refreshVideo(7L);
        Assert.assertEquals(r.getMsg(), ResultEnum.ACCEPTED.getMsg());
    }

    @Test
    @Transactional
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void testRefreshAuthorNotExists() {
        Result<?> r = userService.refreshAuthor(-404L);
    }
}
