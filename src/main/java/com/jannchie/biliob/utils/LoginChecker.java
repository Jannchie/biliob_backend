package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author jannchie
 */
@Component
public class LoginChecker {
    private static UserRepository userRepository;
    private static MongoTemplate mongoTemplate;

    @Autowired
    public LoginChecker(UserRepository userRepository, MongoTemplate mongoTemplate) {
        LoginChecker.userRepository = userRepository;
        LoginChecker.mongoTemplate = mongoTemplate;
    }

    public static User getPasswdAndRole(String name) {
        Query query = Query.query(Criteria.where("name").is(name));
        query.fields().include("password").include("role");
        return mongoTemplate.findOne(query, User.class);
    }


    public static User getUserByUsername(String name) {
        return userRepository.getUserInfo(name);
    }
}
