package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    /**
     * check and return user information
     *
     * @return null: user has not logged in || User: user information
     */
    public static User checkInfo() {
        return userRepository.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
    }


    /**
     * check and return user information and password
     *
     * @return null: user has not logged in || User: user information
     */
    public static User check() {
        return userRepository.findByName((String) SecurityUtils.getSubject().getPrincipal());
    }
}
