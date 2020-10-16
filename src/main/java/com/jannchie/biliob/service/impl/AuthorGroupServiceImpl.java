package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.*;
import com.jannchie.biliob.service.AuthorGroupService;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.CreditService;
import com.jannchie.biliob.utils.AuthorUtil;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import com.mongodb.client.result.DeleteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jannchie
 */
@Service
public class AuthorGroupServiceImpl implements AuthorGroupService {

    final Logger logger = LogManager.getLogger();
    final MongoTemplate mongoTemplate;
    final AuthorService authorService;
    final AuthorUtil authorUtil;
    final private CreditService creditService;

    @Autowired
    public AuthorGroupServiceImpl(MongoTemplate mongoTemplate, AuthorService authorService, AuthorUtil authorUtil, CreditService creditService) {
        this.mongoTemplate = mongoTemplate;
        this.authorService = authorService;
        this.authorUtil = authorUtil;
        this.creditService = creditService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AuthorGroup> initAuthorList(String name, String desc, List<String> tag) {
        AuthorGroup authorGroup = new AuthorGroup(name, desc, tag, UserUtils.getUserId());
        User user = UserUtils.getUser();
        UserUtils.setUserTitleAndRankAndUpdateRole(user);
        if ("观测者".equals(user.getTitle()) || "观想者".equals(user.getTitle()) || "管理者".equals(user.getTitle()) || "追寻者".equals(user.getTitle())) {
            Result<?> r = creditService.doCreditOperation(CreditConstant.INIT_AUTHOR_LIST, CreditConstant.INIT_AUTHOR_LIST.getMsg(name));
            AuthorGroup a = mongoTemplate.save(authorGroup);
            return ResultEnum.SUCCEED.getResult(a, UserUtils.getUser());
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
    }

    @Override
    public Result<?> editAuthorList(String gid, String name, String desc, List<String> tagList) {
        ObjectId userId = UserUtils.getUserId();
        AuthorGroup ag = getAuthorList(gid);
        if (ag == null) {
            return new Result<>(ResultEnum.NOT_FOUND);
        }
        if (userId == null) {
            return new Result<>(ResultEnum.USER_NOT_EXIST);
        }
        if (hasPermission(userId, ag)) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(new ObjectId(gid))), Update.update("name", name).set("desc", desc).set("tagList", tagList), AuthorGroup.class);
        } else {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> setAuthorListInfo(String id, String name, String desc, List<String> tag) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(id)),
                Update.update("name", name).currentDate("updateTime").set("desc", desc).set("tag", tag),
                AuthorGroup.class);
        return creditService.doCreditOperation(CreditConstant.MODIFY_AUTHOR_LIST_INFO, CreditConstant.MODIFY_AUTHOR_LIST_INFO.getMsg(id));
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
    @Transactional(rollbackFor = Exception.class)
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
        UserStarAuthorGroup data = mongoTemplate.findOne(Query.query(Criteria.where(groupIdField).is(new ObjectId(objectId)).and(userIdField).is(user.getId())), UserStarAuthorGroup.class);
        if (data != null) {
            if (data.getStarring()) {
                return new Result<>(ResultEnum.ALREADY_LIKE);
            } else {
                long stars = mongoTemplate.count(Query.query(Criteria.where(groupIdField).is(objectId).and("starring").is(true)), UserStarAuthorGroup.class);
                mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), Update.update("stars", stars + 1), AuthorGroup.class);
                return new Result<>(ResultEnum.SUCCEED);
            }
        }


        Result<?> r = creditService.doCreditOperation(CreditConstant.STAR_AUTHOR_LIST, CreditConstant.STAR_AUTHOR_LIST.getMsg(objectId));
        long stars = mongoTemplate.count(Query.query(Criteria.where(groupIdField).is(objectId).and("starring").is(true)), UserStarAuthorGroup.class);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), Update.update("stars", stars + 1), AuthorGroup.class);
        UserStarAuthorGroup d = mongoTemplate.save(new UserStarAuthorGroup(user.getId(), new ObjectId(objectId)));
        User u = UserUtils.getUserById(authorGroup.getMaintainer().getId());
        Result<?> res = creditService.doCreditOperation(u, CreditConstant.BE_STARED_AUTHOR_LIST, CreditConstant.BE_STARED_AUTHOR_LIST.getMsg(objectId));
        return r;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> forkAuthorList(String objectId) {
        AuthorGroup authorGroup = this.getAuthorList(objectId);
        if (authorGroup == null) {
            return new Result<>(ResultEnum.LIST_NOT_FOUND);
        }
        User u = UserUtils.getUser();
        Result<?> res1 = creditService.doCreditOperation(CreditConstant.FORK_AUTHOR_LIST, CreditConstant.FORK_AUTHOR_LIST.getMsg(objectId));
        Result<?> res2 = creditService.doCreditOperation(UserUtils.getUserById(authorGroup.getMaintainer().getId()), CreditConstant.BE_FORKED_AUTHOR_LIST, CreditConstant.BE_FORKED_AUTHOR_LIST.getMsg(objectId));
        authorGroup.setMaintainer(u);
        authorGroup.setId(null);
        authorGroup.setForkTime(Calendar.getInstance().getTime());
        mongoTemplate.save(authorGroup);
        return res1;
    }

    public List<AuthorGroup> listAuthorList(MatchOperation match, Long page, Integer pageSize) {
        List<AuthorGroup> a = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        match,
                        Aggregation.sort(Sort.by("stars").descending()),
                        Aggregation.skip((page - 1) * pageSize),
                        Aggregation.limit(pageSize),
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.lookup("user_star_author_group", "_id", "groupId", "starList"),
                        Aggregation.lookup("author_group_item", "_id", "gid", "midList"),
                        Aggregation.lookup("author", "midList.mid", "mid", "authorList"),
                        Aggregation.project("starList", "tagList", "name", "desc")
                                .andExpression("{ userId: 1}").as("starList")
                                .andExpression("{ face: 1, mid: 1}").as("authorList")
                                .andExpression("{ nickName: 1, _id: 1 }").as("creator")
                                .andExpression("{ nickName: 1, _id: 1 }").as("maintainer")
                ), AuthorGroup.class, AuthorGroup.class
        ).getMappedResults();
        ObjectId userId = UserUtils.getUserId();
        a.forEach(authorGroup -> {
            authorGroup.setStars(authorGroup.getStarList().size());
            authorGroup.setAuthors(authorGroup.getAuthorList().size());
            authorGroup.setAuthorList(authorGroup.getAuthorList().subList(0, authorGroup.getAuthors() > 5 ? 5 : authorGroup.getAuthors()));
            if (userId == null) {
                return;
            }
            setIsStared(userId, authorGroup);
            authorGroup.setStarList(null);
        });
        return a;
    }

    @Override
    public List<AuthorGroup> listAuthorList(String keyword, Long page, Integer pageSize) {
        MatchOperation match;
        if (!"".equals(keyword)) {
            match = Aggregation.match(new Criteria().orOperator(Criteria.where("name").regex(keyword, "i"), Criteria.where("desc").regex(keyword, "i"), Criteria.where("tagList").is(keyword)));
        } else {
            match = Aggregation.match(new Criteria());
        }
        return this.listAuthorList(match, page, pageSize);
    }

    private void setIsStared(ObjectId userId, AuthorGroup authorGroup) {
        authorGroup.setStared(false);
        for (UserStarAuthorGroup u : authorGroup.getStarList()) {
            if (u.getUserId().equals(userId) && u.getStarring()) {
                authorGroup.setStared(true);
                break;
            }
        }
    }

    public AuthorGroup getAuthorList(ObjectId objectId) {
        ObjectId userId = UserUtils.getUserId();
        mongoTemplate.save(new ObjectVisitRecord("AuthorGroup", userId, objectId, Calendar.getInstance().getTime()));
        return getAuthorList(Aggregation.match(Criteria.where("_id").is(objectId)));
    }

    public AuthorGroup getAuthorList(MatchOperation match) {
        AuthorGroup a = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        match,
                        Aggregation.lookup("user", "creator._id", "_id", "creator"),
                        Aggregation.unwind("creator"),
                        Aggregation.lookup("user", "maintainer._id", "_id", "maintainer"),
                        Aggregation.unwind("maintainer"),
                        Aggregation.lookup("user_star_author_group", "_id", "groupId", "starList"),
                        Aggregation.lookup("author_group_item", "_id", "gid", "midList"),
                        Aggregation.lookup("author", "midList.mid", "mid", "authorList"),
                        Aggregation.project("starList", "tagList", "name", "desc", "authorList")
                                .andExpression("{ userId: 1}").as("starList")
                                .andExpression("{ nickName: 1, _id: 1 }").as("creator")
                                .andExpression("{ nickName: 1, _id: 1 }").as("maintainer"),
                        Aggregation.project().andExpression("{data: 0, keyword: 0}").as("authorList")
                ), AuthorGroup.class, AuthorGroup.class
        ).getUniqueMappedResult();
        if (a != null) {
            a.setStars(a.getStarList().size());
            setIsStared(UserUtils.getUserId(), a);
            a.setStarList(null);
            a.setAuthors(a.getAuthorList().size());
            authorUtil.getInterval(a.getAuthorList());
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            a.getAuthorList().stream().filter(author -> author.getRank() == null || author.getRank().getUpdateTime() == null || author.getRank().getUpdateTime().before(c.getTime())).forEach(authorService::getRankData);
        }
        return a;
    }

    @Override
    public AuthorGroup getAuthorList(String objectId) {
        return getAuthorList(new ObjectId(objectId));
    }


    @Override
    public Result<?> unstarAuthorList(String objectId) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        mongoTemplate.updateFirst(Query.query(Criteria.where("groupId").is(new ObjectId(objectId)).and("userId").is(userId)), Update.update("starring", false), UserStarAuthorGroup.class);
        long stars = mongoTemplate.count(Query.query(Criteria.where("groupId").is(objectId)), UserStarAuthorGroup.class);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(objectId)), Update.update("stars", stars), AuthorGroup.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    public List<AuthorGroup> listUserAuthorList(Integer page, Integer pageSize, int type) {
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return null;
        }
        if (type == 0) {
            List<ObjectId> gidList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserStarAuthorGroup.class).stream().map(UserStarAuthorGroup::getGroupId).collect(Collectors.toList());
            return this.listAuthorList(Aggregation.match(Criteria.where("_id").in(gidList)), Long.valueOf(page), pageSize);
        } else if (type == 1) {
            return this.listAuthorList(Aggregation.match(Criteria.where("maintainer._id").is(userId)), Long.valueOf(page), pageSize);
        }
        return null;
    }

    @Override
    public Result<?> deleteAuthorFromGroup(String gid, Long mid) {
        ObjectId groupId = new ObjectId(gid);
        ObjectId userId = UserUtils.getUserId();
        if (userId == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        if (hasPermission(userId, groupId)) {
            mongoTemplate.remove(Query.query(Criteria.where("mid").is(mid).and("gid").is(groupId)), AuthorGroupItem.class);
            addUpdateLog(userId, groupId, String.format("移除mid为%s的UP主", mid));
            return new Result<>(ResultEnum.SUCCEED);
        } else {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
    }

    private boolean hasPermission(ObjectId userId, ObjectId groupId) {
        AuthorGroup ag = getAuthorList(groupId);
        return userId == null || userId.equals(ag.getMaintainer().getId()) || userId.equals(ag.getCreator().getId());
    }

    private boolean hasPermission(ObjectId userId, AuthorGroup ag) {
        return userId.equals(ag.getMaintainer().getId()) || userId.equals(ag.getCreator().getId());
    }

    @Override
    public Result<?> addAuthorToGroup(String gid, Long mid) {
        ObjectId groupId = new ObjectId(gid);
        ObjectId userId = UserUtils.getUserId();
        if (!hasPermission(userId, groupId)) {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
        if (!mongoTemplate.exists(Query.query(Criteria.where("mid").is(mid)), Author.class)) {
            return new Result<>(ResultEnum.NOT_OBSERVING);
        }
        mongoTemplate.upsert(Query.query(Criteria.where("mid").is(mid).and("gid").is(groupId)), Update.update("mid", mid).set("gid", new ObjectId(gid)), AuthorGroupItem.class);
        addUpdateLog(userId, groupId, String.format("添加mid为%s的UP主", mid));
        return new Result<>(ResultEnum.SUCCEED);
    }

    private void addUpdateLog(ObjectId userId, ObjectId gid, String message) {
        GroupUpdateRecord gur = new GroupUpdateRecord();
        gur.setMessage(message);
        gur.setUserId(userId);
        gur.setDate(Calendar.getInstance().getTime());
        gur.setGid(gid);
        mongoTemplate.insert(gur);
    }

    @Override
    public List<GroupUpdateRecord> listChangeLog(String gid) {
        Aggregation a = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("gid").is(new ObjectId(gid))),
                Aggregation.lookup("user", "userId", "_id", "user"),
                Aggregation.unwind("user"),
                Aggregation.project("message", "date").andExpression("{'nickName': 1}").as("user")
        );
        return mongoTemplate.aggregate(a, GroupUpdateRecord.class, GroupUpdateRecord.class).getMappedResults();
    }
}
