package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.AuthorList;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.AuthorListService;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author jannchie
 */
@Service
public class AuthorListServiceImpl implements AuthorListService {

    final MongoTemplate mongoTemplate;
    final CreditOperateHandle creditOperateHandle;

    @Autowired
    public AuthorListServiceImpl(MongoTemplate mongoTemplate, CreditOperateHandle creditOperateHandle) {
        this.mongoTemplate = mongoTemplate;
        this.creditOperateHandle = creditOperateHandle;
    }

    @Override
    public Result<AuthorList> initAuthorList(String name, String desc, List<String> tag) {
        AuthorList authorList = new AuthorList(name, desc, tag, UserUtils.getUserId());
        User user = UserUtils.getUser();
        User userBlank = new User(user.getId());
        authorList.setStarList(new ArrayList<>(Collections.singleton(userBlank)));
        creditOperateHandle.doCreditOperate(user, CreditConstant.INIT_AUTHOR_LIST, name, () -> mongoTemplate.save(authorList));
        return new Result<>(ResultEnum.SUCCEED, authorList);
    }

    @Override
    public Result<UpdateResult> setAuthorListInfo(String id, String name, String desc, List<String> tag) {
        return creditOperateHandle.doCreditOperate(
                UserUtils.getUser(), CreditConstant.MODIFY_AUTHOR_LIST_INFO, id, () ->
                        mongoTemplate.updateFirst(
                                Query.query(Criteria.where("_id").is(id)),
                                Update.update("name", name).currentDate("updateTime").set("desc", desc).set("tag", tag),
                                AuthorList.class));
    }

    @Override
    public Result<DeleteResult> deleteAuthorList(String objectId) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        AuthorList authorList = this.getAuthorList(objectId);

        if (authorList == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }
        if (authorList.getMaintainer().getId().toHexString().equals(userId.toHexString())) {
            return new Result<>(ResultEnum.SUCCEED, mongoTemplate.remove(Query.query(Criteria.where("_id").is(objectId)), AuthorList.class));
        } else {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
    }

    @Override
    public Result<?> addAuthorToAuthorList(String objectId, Long mid) {
        Author author = new Author();
        author.setMid(mid);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().addToSet("authorList", author), AuthorList.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public Result<?> removeAuthorFromAuthorList(String objectId, Long mid) {
        Author author = new Author();
        author.setMid(mid);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().pull("authorList", author), AuthorList.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public Result<?> starAuthorList(String objectId) {
        User user = UserUtils.getUser();

        AuthorList authorList = this.getAuthorList(objectId);

        if (authorList == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }

        for (User startedUser : authorList.getStarList()
        ) {
            if (startedUser.getId().toHexString().equals(user.getId().toHexString())) {
                return new Result<>(ResultEnum.ALREADY_LIKE);
            }
        }

        return creditOperateHandle.doCreditOperate(
                user, CreditConstant.STAR_AUTHOR_LIST, () -> {
                    ObjectId userId = UserUtils.getUserId();
                    User blankUser = new User(userId);
                    creditOperateHandle.doCreditOperate(authorList.getMaintainer(), CreditConstant.BE_STARED_AUTHOR_LIST, authorList.getId(), () -> null);
                    return mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().addToSet("starList", blankUser), AuthorList.class);
                }
        );
    }

    @Override
    public Result<?> forkAuthorList(String objectId) {
        AuthorList authorList = this.getAuthorList(objectId);
        if (authorList == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }
        User user = UserUtils.getUser();
        return creditOperateHandle.doCreditOperate(user, CreditConstant.FORK_AUTHOR_LIST, () -> {
            creditOperateHandle.doCreditOperate(authorList.getMaintainer(), CreditConstant.BE_FORKED_AUTHOR_LIST, authorList.getId(), () -> null);
            authorList.setMaintainer(user);
            authorList.setForkTime(Calendar.getInstance().getTime());
            return null;
        });
    }

    @Override
    public List<AuthorList> listAuthorList(String keyword, Long page, Integer pageSize) {
        if (!"".equals(keyword)) {
            AggregationOperation match = Aggregation.match(new Criteria().orOperator(Criteria.where("name").regex(keyword), Criteria.where("desc").regex(keyword), Criteria.where("tag").is(keyword)));
        }
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.skip((page - 1) * pageSize),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("creator"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("maintainer")
                ), AuthorList.class, AuthorList.class
        ).getMappedResults();
    }

    @Override
    public AuthorList getAuthorList(String objectId) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("_id").is(objectId)),
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("creator"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("maintainer")
                ), AuthorList.class, AuthorList.class
        ).getUniqueMappedResult();
    }


    @Override
    public Result<?> unstarAuthorList(String objectId) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().pull("starList", Query.query(Criteria.where("_id").is(userId))), AuthorList.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public List<AuthorList> listUserAuthorList(Integer page, Integer pageSize, int type) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return null;
        }
        String field;
        if (type == 0) {
            field = "starList._id";
        } else if (type == 1) {
            field = "maintainer._id";
        } else {
            field = "creator._id";
        }
        return mongoTemplate.find(Query.query(Criteria.where(field).is(userId)).with(PageRequest.of(page - 1, pageSize, Sort.by("_id").descending())), AuthorList.class);
    }
}
