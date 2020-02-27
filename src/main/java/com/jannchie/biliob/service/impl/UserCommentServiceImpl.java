package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditHandle;
import com.jannchie.biliob.model.Comment;
import com.jannchie.biliob.model.User;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class UserCommentServiceImpl implements UserCommentService {
    private static final Logger logger = LogManager.getLogger(UserCommentServiceImpl.class);
    private static MongoTemplate mongoTemplate;
    private CreditHandle creditHandle;

    @Autowired
    public UserCommentServiceImpl(MongoTemplate mongoTemplate, CreditHandle creditHandle) {
        UserCommentServiceImpl.mongoTemplate = mongoTemplate;
        this.creditHandle = creditHandle;
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
                        Aggregation.match(Criteria.where("path").is(path)),
                        sortAggregationOperation,
                        Aggregation.skip(page * pageSize),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("user", "userId", "_id", "user"),
                        Aggregation.unwind("user"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("user")
                ), Comment.class, Comment.class);
        List<Comment> result = ar.getMappedResults();
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

    @Override
    public ResponseEntity<Result<String>> postComment(Comment comment) {
        User user = UserUtils.getUser();
        Integer mapExp = 100;
        if (user.getExp() < mapExp) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.EXP_NOT_ENOUGH));
        }
        return creditHandle.doCreditOperation(user, CreditConstant.POST_COMMENT, () -> {
            comment.setDate(Calendar.getInstance().getTime());
            comment.setLikeList(new ArrayList<>());
            comment.setDisLikeList(new ArrayList<>());
            comment.setUserId(user.getId());
            mongoTemplate.save(comment);
            return comment.getPath();
        });
    }

    @Override
    public ResponseEntity<Result<String>> likeComment(String commentId) {

        User user = UserUtils.getUser();
        if (mongoTemplate.exists(Query.query(Criteria.where("likeList").is(user.getId()).and("_id").is(commentId)), Comment.class)) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.ALREADY_LIKE));
        }
        Comment comment = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
        if (comment == null) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.EXECUTE_FAILURE));
        }
        ObjectId commentPublisherId = comment.getUserId();

        User publisher = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentPublisherId)), User.class);
        return creditHandle.doCreditOperation(user, CreditConstant.LIKE_COMMENT, () -> {
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(commentId)), new Update().addToSet("likeList", user.getId()).inc("like", 1), Comment.class);
            creditHandle.doCreditOperation(publisher, CreditConstant.BE_LIKE_COMMENT, () -> {
                return commentId;
            });
            return commentId;
        });

    }

    @Override
    public ResponseEntity<Result<?>> dislikeComment(String commentId) {
        return null;
    }

    @Override
    public ResponseEntity<Result<?>> deleteComment(String commentId) {
        User user = UserUtils.getUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.HAS_NOT_LOGGED_IN));
        }
        Comment comment = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
        if (comment == null) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.COMMENT_NOT_FOUND));
        }
        ObjectId commentPublisherId = comment.getUserId();
        if (commentPublisherId.equals(user.getId())) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(commentId)), Comment.class);
            return ResponseEntity.ok(new Result<>(ResultEnum.SUCCEED));
        } else {
            return ResponseEntity.ok(new Result<>(ResultEnum.EXECUTE_FAILURE));
        }
    }
}
