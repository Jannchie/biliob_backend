package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.apache.shiro.SecurityUtils;

/**
 * @author jannchie
 */
public class LoginCheck {

  /**
   * check and return user information and password
   *
   * @param userRepository user's dao
   * @return null: user has not logged in || User: user information
   */
  public static User check(UserRepository userRepository) {
    User user = userRepository.findByName((String) SecurityUtils.getSubject().getPrincipal());
    if (user == null) {
      return null;
    }
    return user;
  }

  /**
   * check and return user information
   *
   * @param userRepository user's dao
   * @return null: user has not logged in || User: user information
   */
  public static User checkInfo(UserRepository userRepository) {
        User user = userRepository.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
        if (user == null) {
          return null;
        }
        return user;
    }
}
