package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jannchie
 */
@Component
public class LoginCheck {
  private static UserRepository userRepository;

  @Autowired
  public LoginCheck(UserRepository userRepository) {
    LoginCheck.userRepository = userRepository;
  }

  /**
   * check and return user information
   *
   * @return null: user has not logged in || User: user information
   */
  public static User checkInfo() {
    User user = userRepository.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
    if (user == null) {
      return null;
    }
    return user;
  }

  /**
   * check and return user information and password
   *
   * @return null: user has not logged in || User: user information
   */
  public User check() {
    User user = userRepository.findByName((String) SecurityUtils.getSubject().getPrincipal());
    if (user == null) {
      return null;
    }
    return user;
  }
}
