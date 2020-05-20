package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Jannchie
 */
@Component
public class UserUtils {
    private static MongoTemplate mongoTemplate;
    private static UserRepository userRepository;
    private static UserService userService;

    @Autowired
    public UserUtils(MongoTemplate mongoTemplate, UserRepository userRepository, UserService userService) {
        UserUtils.mongoTemplate = mongoTemplate;
        UserUtils.userRepository = userRepository;
        UserUtils.userService = userService;
    }

    public static User getUserByUsernameOrMail(String name) {
        return mongoTemplate.findOne(
                Query.query(
                        new Criteria()
                                .orOperator(Criteria.where("name").is(name), Criteria.where("mail").is(name))),
                User.class,
                "user");
    }

    public static User getUser() {
        String username = getUsername();
        return getUserByUsernameOrMail(username);
    }

    public static ObjectId getUserId() {
        User user = getUser();
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static User getPasswdAndRole(String name) {
        Query query = getUserQuery(name);
        query.fields().include("password").include("role").include("name");
        return mongoTemplate.findOne(query, User.class);
    }

    private static Query getUserQuery(String name) {
        return Query.query(
                new Criteria()
                        .orOperator(Criteria.where("name").is(name), Criteria.where("mail").is(name)));
    }

    public static User getFullInfo() {
        String username = getUsername();
        Query query = getUserQuery(username);
        User user = mongoTemplate.findOne(query, User.class);
        if (user != null) {
            setUserTitleAndRankAndUpdateRole(user);
        }
        return user;
    }

    public static void updateUserCreditAndExp(User user, Double credit) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(user.getName())),
                Update.update("credit", BigDecimal.valueOf(user.getCredit() - credit).setScale(2, BigDecimal.ROUND_HALF_DOWN)).set("exp", user.getExp() + credit), User.class);
    }

    public static void setUserTitleAndRankAndUpdateRole(User user) {
        long rank = mongoTemplate.count(Query.query(Criteria.where("exp").gte(user.getExp())), "user");
        RoleEnum roleEnum = RoleEnum.NORMAL_USER;
        user.setRank(Math.toIntExact(rank));
        if (rank <= 3) {
            user.setTitle("管理者");
            roleEnum = RoleEnum.ADMIN;
        } else if (rank <= 3 + 16) {
            user.setTitle("观测者");
            roleEnum = RoleEnum.OBSERVER;
        } else if (rank <= 3 + 16 + 50) {
            user.setTitle("观想者");
        } else if (user.getExp() <= 100) {
            user.setTitle("初心者");
        } else {
            long count = userService.getUserCount();
            if (rank < count / 20) {
                user.setTitle("追寻者");
            } else {
                user.setTitle("彷徨者");
            }
        }
        if (!roleEnum.getName().equals(user.getRole())) {
            Integer preLevel = RoleEnum.getLevelByName(user.getRole());
            if (preLevel < roleEnum.getLevel() || "普通用户".equals(user.getRole())) {
                mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(user.getName())), Update.update("role", roleEnum.getName()), User.class);
            }
        }
    }

    public static User getUserById(ObjectId id) {
        Query q = Query.query(Criteria.where("_id").is(id));
        q.fields().exclude("favoriteAid").exclude("favoriteMid");
        return mongoTemplate.findOne(q, User.class);
    }
}
