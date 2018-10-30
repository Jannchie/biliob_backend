package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.apache.shiro.SecurityUtils;

/**
 * @author jannchie
 */

public class LoginCheck {

    public static User check(UserRepository userRepository) {
        User user = userRepository.findByName((String) SecurityUtils.getSubject().getPrincipal());
        if (user == null) {
            throw new org.apache.shiro.authc.AccountException("请先登录！");
        }
        return user;
    }

    public static User checkInfo(UserRepository userRepository) {
        User user = userRepository.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
        if (user == null) {
            throw new org.apache.shiro.authc.AccountException("请先登录！");
        }
        return user;
    }
}
