package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.DbFields;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.TestConstants;
import com.jannchie.biliob.model.Comment;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.bson.types.ObjectId;
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
@Transactional
public class UserCommentServiceImplTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserCommentServiceImpl userCommentService;
    @Autowired
    private UserUtils userUtils;


    @Test
    @WithMockUser(username = TestConstants.NORMAL_USER_NAME)
    public void postCommentExpNotEnough() {
        User u = userUtils.getUser();
        Comment c = new Comment();
        String path = "/a/path/of/comment/unique";
        String content = "这是一段测试文字，我希望没有人发送同样的内容，否则测试可能无法通过。";
        c.setUser(u);
        c.setLike(123);
        c.setDate(Calendar.getInstance().getTime());
        c.setContent(content);
        c.setPath(path);
        Result<?> r = userCommentService.postComment(c);
        Assert.assertEquals(r.getMsg(), ResultEnum.EXP_NOT_ENOUGH.getMsg());
    }

    @Test
    @WithMockUser(username = TestConstants.PERMITTED_NAME)
    public void postComment() {
        User u = userUtils.getUser();
        Comment c = new Comment();
        String path = "/a/path/of/comment/unique";
        String content = "这是一段测试文字，我希望没有人发送同样的内容，否则测试可能无法通过。";
        c.setUser(u);
        c.setLike(123);
        c.setDate(Calendar.getInstance().getTime());
        c.setContent(content);
        c.setPath(path);
        Result<?> r = userCommentService.postComment(c);
        Assert.assertEquals(r.getMsg(), ResultEnum.SUCCEED.getMsg());
        Comment comment = userCommentService.listComments(path, 0, 1, 0).get(0);
        Assert.assertNotNull(comment);
        Assert.assertEquals(comment.getLike().longValue(), 0L);
        Assert.assertEquals(content, comment.getContent());

        User user = userUtils.getUser();
        Assert.assertEquals(user.getCredit(), u.getCredit() + CreditConstant.POST_COMMENT.getValue(), 0);
        Assert.assertEquals(user.getExp(), u.getExp() - CreditConstant.POST_COMMENT.getValue(), 0);
    }

    @Test
    @WithMockUser(username = TestConstants.PERMITTED_NAME)
    public void likeComment() {
        User u = userUtils.getUser();
        // 找一个没有喜欢过的
        Comment c = mongoTemplate.findOne(Query.query(Criteria.where("likeList").ne(u.getId()).and("userId").ne(u.getId())), Comment.class);
        assert c != null;
        ObjectId uid = c.getUserId();
        User publisherBef = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(uid)), User.class);
        userCommentService.likeComment(c.getCommentId());
        User user = userUtils.getUser();
        Assert.assertEquals(user.getCredit(), u.getCredit() + CreditConstant.LIKE_COMMENT.getValue(), 0);
        Assert.assertEquals(user.getExp(), u.getExp() - CreditConstant.LIKE_COMMENT.getValue(), 0);
        User publisherAft = mongoTemplate.findOne(Query.query(Criteria.where(DbFields.ID).is(uid)), User.class);
        assert publisherAft != null;
        assert publisherBef != null;
        Assert.assertEquals(publisherAft.getCredit(), publisherBef.getCredit() + CreditConstant.BE_LIKE_COMMENT.getValue(), 0);
        Assert.assertEquals(publisherAft.getExp(), publisherBef.getExp() + CreditConstant.BE_LIKE_COMMENT.getValue(), 0);
    }
}