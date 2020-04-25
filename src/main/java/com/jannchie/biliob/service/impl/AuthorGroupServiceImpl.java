package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.*;
import com.jannchie.biliob.service.AuthorGroupService;
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

import java.util.Calendar;
import java.util.List;

/**
 * @author jannchie
 */
@Service
public class AuthorGroupServiceImpl implements AuthorGroupService {

    final MongoTemplate mongoTemplate;
    final CreditOperateHandle creditOperateHandle;

    @Autowired
    public AuthorGroupServiceImpl(MongoTemplate mongoTemplate, CreditOperateHandle creditOperateHandle) {
        this.mongoTemplate = mongoTemplate;
        this.creditOperateHandle = creditOperateHandle;
    }

    @Override
    public Result<AuthorGroup> initAuthorList(String name, String desc, List<String> tag) {
        AuthorGroup authorGroup = new AuthorGroup(name, desc, tag, UserUtils.getUserId());
        User user = UserUtils.getUser();
        creditOperateHandle.doCreditOperate(user, CreditConstant.INIT_AUTHOR_LIST, name, () -> {
            AuthorGroup a = mongoTemplate.save(authorGroup);
            this.starAuthorList(a.getId());
            return a;
        });
        return new Result<>(ResultEnum.SUCCEED, authorGroup);
    }

    @Override
    public Result<UpdateResult> setAuthorListInfo(String id, String name, String desc, List<String> tag) {
        return creditOperateHandle.doCreditOperate(
                UserUtils.getUser(), CreditConstant.MODIFY_AUTHOR_LIST_INFO, id, () ->
                        mongoTemplate.updateFirst(
                                Query.query(Criteria.where("_id").is(id)),
                                Update.update("name", name).currentDate("updateTime").set("desc", desc).set("tag", tag),
                                AuthorGroup.class));
    }

    @Override
    public Result<DeleteResult> deleteAuthorList(String objectId) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        AuthorGroup authorGroup = this.getAuthorList(objectId);

        if (authorGroup == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }
        if (authorGroup.getMaintainer().getId().toHexString().equals(userId.toHexString())) {
            return new Result<>(ResultEnum.SUCCEED, mongoTemplate.remove(Query.query(Criteria.where("_id").is(objectId)), AuthorGroup.class));
        } else {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
    }

    @Override
    public Result<?> addAuthorToAuthorList(String objectId, Long mid) {
        Author author = new Author();
        author.setMid(mid);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().addToSet("authorList", author), AuthorGroup.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public Result<?> removeAuthorFromAuthorList(String objectId, Long mid) {
        Author author = new Author();
        author.setMid(mid);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), new Update().pull("authorList", author), AuthorGroup.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public Result<?> starAuthorList(String objectId) {
        User user = UserUtils.getUser();

        AuthorGroup authorGroup = this.getAuthorList(objectId);

        if (user == null) {
            return new Result<>(ResultEnum.USER_NOT_EXIST);
        }
        if (authorGroup == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }

        String groupIdField = "groupId";
        String userIdField = "userId";
        if (mongoTemplate.exists(Query.query(Criteria.where(groupIdField).is(new ObjectId(objectId)).and(userIdField).is(user.getId())), UserStarAuthorGroup.class)) {
            return new Result<>(ResultEnum.ALREADY_LIKE);
        }

        return creditOperateHandle.doCreditOperate(
                user, CreditConstant.STAR_AUTHOR_LIST, () -> {
                    creditOperateHandle.doCreditOperate(authorGroup.getMaintainer(), CreditConstant.BE_STARED_AUTHOR_LIST, authorGroup.getId(), () -> null);
                    return mongoTemplate.save(new UserStarAuthorGroup(user.getId(), new ObjectId(objectId)));
                }
        );
    }

    @Override
    public Result<?> forkAuthorList(String objectId) {
        AuthorGroup authorGroup = this.getAuthorList(objectId);
        if (authorGroup == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }
        User user = UserUtils.getUser();
        return creditOperateHandle.doCreditOperate(user, CreditConstant.FORK_AUTHOR_LIST, () -> {
            creditOperateHandle.doCreditOperate(authorGroup.getMaintainer(), CreditConstant.BE_FORKED_AUTHOR_LIST, authorGroup.getId(), () -> null);
            authorGroup.setMaintainer(user);
            authorGroup.setForkTime(Calendar.getInstance().getTime());
            return null;
        });
    }

    @Override
    public List<AuthorGroup> listAuthorList(String keyword, Long page, Integer pageSize) {
        if (!"".equals(keyword)) {
            AggregationOperation match = Aggregation.match(new Criteria().orOperator(Criteria.where("name").regex(keyword), Criteria.where("desc").regex(keyword), Criteria.where("tag").is(keyword)));
        }
        List<AuthorGroup> a = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.skip((page - 1) * pageSize),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.lookup("user_star_author_group", "_id", "groupId", "starList"),
                        Aggregation.lookup("author_group_item", "_id", "gid", "midList"),
                        Aggregation.lookup("author", "midList.mid", "mid", "authorList"),
                        Aggregation.project().andExpression("{ _id: 0, groupId: 0 }").as("starList"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("creator"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("maintainer")
                ), AuthorGroup.class, AuthorGroup.class
        ).getMappedResults();
        ObjectId userId = UserUtils.getUserId();
        a.forEach(authorGroup -> {
            authorGroup.setStars(authorGroup.getStarList().size());
            if (userId == null) {
                return;
            }
            setIsStared(userId, authorGroup);
            authorGroup.setStarList(null);
        });
        return a;
    }

    private void setIsStared(ObjectId userId, AuthorGroup authorGroup) {
        authorGroup.setStared(false);
        for (UserStarAuthorGroup u : authorGroup.getStarList()) {
            if (u.getUserId().equals(userId)) {
                authorGroup.setStared(true);
                break;
            }
        }
    }

    @Override
    public AuthorGroup getAuthorList(String objectId) {
        AuthorGroup a = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("_id").is(objectId)),
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.lookup("user_star_author_group", "_id", "groupId", "starList"),
                        Aggregation.lookup("author_group_item", "_id", "gid", "midList"),
                        Aggregation.lookup("author", "midList.mid", "mid", "authorList"),
                        Aggregation.project().andExpression("{ _id: 0, groupId: 0 }").as("starList"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("creator"),
                        Aggregation.project().andExpression("{ password: 0, favoriteMid: 0, favoriteAid: 0 }").as("maintainer")
                ), AuthorGroup.class, AuthorGroup.class
        ).getUniqueMappedResult();
        if (a != null) {
            setIsStared(UserUtils.getUserId(), a);
            a.setStarList(null);
        }
        return a;
    }


    @Override
    public Result<?> unstarAuthorList(String objectId) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        mongoTemplate.remove(Query.query(Criteria.where("groupId").is(objectId).and("userId").is(userId)), UserStarAuthorGroup.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public List<AuthorGroup> listUserAuthorList(Integer page, Integer pageSize, int type) {
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
        return mongoTemplate.find(Query.query(Criteria.where(field).is(userId)).with(PageRequest.of(page - 1, pageSize, Sort.by("_id").descending())), AuthorGroup.class);
    }

    @Override
    public Result<?> deleteAuthorFromGroup(String gid, Long mid) {
        mongoTemplate.remove(Query.query(Criteria.where("mid").is(mid).and("gid").is(gid)), AuthorGroupItem.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public Result<?> addAuthorToGroup(String gid, Long mid) {
        ObjectId groupId = new ObjectId(gid);
        mongoTemplate.upsert(Query.query(Criteria.where("mid").is(mid).and("gid").is(groupId)), Update.update("mid", mid).set("gid", new ObjectId(gid)), AuthorGroupItem.class);
        addUpdateLog(groupId, String.format("添加mid为%s的UP主", mid));
        return new Result<>(ResultEnum.SUCCEED);
    }

    private void addUpdateLog(ObjectId gid, String message) {
        GroupUpdateRecord gur = new GroupUpdateRecord();
        gur.setMessage(message);
        gur.setUserId(UserUtils.getUserId());
        gur.setDate(Calendar.getInstance().getTime());
        mongoTemplate.updateFirst(Query.query(Criteria.where("groupId").is(gid)), new Update().push("updateRecord", gur), AuthorGroup.class);
    }
}
