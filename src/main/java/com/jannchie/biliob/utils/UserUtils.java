package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Jannchie
 */
@Component
public class UserUtils {
    private static MongoTemplate mongoTemplate;
    private static UserRepository userRepository;

    @Autowired
    public UserUtils(MongoTemplate mongoTemplate, UserRepository userRepository) {
        UserUtils.mongoTemplate = mongoTemplate;
        UserUtils.userRepository = userRepository;
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

    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static User getPasswdAndRole(String name) {
        Query query = getUserQuery(name);
        query.fields().include("password").include("role");
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
        return mongoTemplate.findOne(query, User.class);
    }
}
