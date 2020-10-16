package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.model.Comment;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.CreditService;
import com.jannchie.biliob.service.UserCommentService;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class UserCommentServiceImpl implements UserCommentService {
    private static final Logger logger = LogManager.getLogger(UserCommentServiceImpl.class);
    private static MongoTemplate mongoTemplate;
    private final CreditService creditService;

    @Autowired
    public UserCommentServiceImpl(MongoTemplate mongoTemplate, CreditService creditService) {
        UserCommentServiceImpl.mongoTemplate = mongoTemplate;
        this.creditService = creditService;
    }


    @Override
    public List<Comment> listComments(String path, Integer page, Integer pageSize, Integer sort) {
        AggregationOperation sortAggregationOperation;
        if (sort == 0) {
            sortAggregationOperation = Aggregation.sort(Sort.Direction.DESC, "like");
        } else {
            sortAggregationOperation = Aggregation.sort(Sort.Direction.DESC, "date");
        }
        AggregationResults<Comment> ar = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("path").is(path).and("parentId").exists(false)),
                        sortAggregationOperation,
                        Aggregation.skip((long) page * pageSize),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("user", "userId", "_id", "user"),
                        Aggregation.unwind("user"),
                        Aggregation.project().andExpression("{password: 0, ip: 0,  favoriteMid: 0, favoriteAid: 0, mail: 0, credit: 0 }").as("user")
                ), Comment.class, Comment.class);
        List<Comment> result = ar.getMappedResults();
        HashMap<String, User> userHashMap = new HashMap<>(20);
        result.forEach(comment -> {
                    setUserMap(userHashMap, comment);
                    List<Comment> replies = mongoTemplate.aggregate(
                            Aggregation.newAggregation(
                                    Aggregation.match(
                                            Criteria.where("parentId").is(comment.getCommentId())),
                                    Aggregation.sort(Sort.by("date").descending()),
                                    Aggregation.lookup("user", "userId", "_id", "user"),
                                    Aggregation.unwind("user"),
                                    Aggregation.project().andExpression("{password: 0, ip: 0,  favoriteMid: 0, favoriteAid: 0, mail: 0, credit: 0 }").as("user")
                            )
                            , Comment.class, Comment.class).getMappedResults();
                    replies.forEach(r -> setUserMap(userHashMap, r));
                    comment.setReplies(replies);
                }
        );

        User user = UserUtils.getUser();

        if (user == null) {
            return result;
        }
        result.forEach((comment) -> {
            if (comment.getLikeList().contains(user.getId())) {
                comment.setLiked(true);
            } else {
                comment.setLiked(false);
            }
            comment.setLike(comment.getLikeList().size());
            comment.setLikeList(null);
        });
        return result;
    }

    private void setUserMap(HashMap<String, User> userHashMap, Comment comment) {
        if (userHashMap.containsKey(comment.getUser().getName())) {
            comment.setUser(userHashMap.get(comment.getUser().getName()));
        } else {
            UserUtils.setUserTitleAndRankAndUpdateRole(comment.getUser());
            userHashMap.put(comment.getUser().getName(), comment.getUser());
        }
    }

    @Override
    public ResponseEntity<Result<Comment>> postComment(Comment comment) {
        User user = UserUtils.getUser();
        Integer need = 100;
        if (user.getExp() < need) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.EXP_NOT_ENOUGH));
        }
        if (mongoTemplate.exists(Query.query(
                Criteria
                        .where("path").is(comment.getPath())
                        .and("userId").is(user.getId())
                        .and("parentId").is(comment.getParentId())
                        .and("content").is(comment.getContent())
                ), Comment.class
        )) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.DUMP_COMMENT));
        }

        comment.setDate(Calendar.getInstance().getTime());
        comment.setLikeList(new ArrayList<>());
        comment.setDisLikeList(new ArrayList<>());
        comment.setUserId(user.getId());
        Result<Comment> c = ResultEnum.SUCCEED.getResult(mongoTemplate.save(comment));
        creditService.doCreditOperation(CreditConstant.POST_COMMENT, CreditConstant.POST_COMMENT.getMsg(comment.getPath()));
        return ResponseEntity.ok(c);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> likeComment(String commentId) {

        User user = UserUtils.getUser();
        if (mongoTemplate.exists(Query.query(Criteria.where("likeList").is(user.getId()).and("_id").is(commentId)), Comment.class)) {
            return new Result<>(ResultEnum.ALREADY_LIKE);
        }
        Comment comment = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
        if (comment == null) {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
        ObjectId commentPublisherId = comment.getUserId();

        User publisher = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentPublisherId)), User.class);

        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(commentId)), new Update().addToSet("likeList", user.getId()).inc("like", 1), Comment.class);
        creditService.doCreditOperation(publisher, CreditConstant.BE_LIKE_COMMENT, CreditConstant.BE_LIKE_COMMENT.getMsg(commentId));
        return creditService.doCreditOperation(user, CreditConstant.LIKE_COMMENT, CreditConstant.LIKE_COMMENT.getMsg(commentId));
    }

    @Override
    public ResponseEntity<Result<?>> dislikeComment(String commentId) {
        return null;
    }

    @Override
    public ResponseEntity<Result<?>> deleteComment(String commentId) {
        User user = UserUtils.getFullInfo();
        if (user == null) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.HAS_NOT_LOGGED_IN));
        }
        Integer level = RoleEnum.getLevelByName(user.getRole());
        Comment comment = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
        if (comment == null) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.COMMENT_NOT_FOUND));
        }
        ObjectId commentPublisherId = comment.getUserId();
        if (commentPublisherId.equals(user.getId()) || level >= 7) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
            mongoTemplate.remove(Query.query(Criteria.where("parentId").is(commentId)), Comment.class);
            return ResponseEntity.ok(new Result<>(ResultEnum.SUCCEED));
        } else {
            return ResponseEntity.ok(new Result<>(ResultEnum.PERMISSION_DENIED));
        }
    }
}
