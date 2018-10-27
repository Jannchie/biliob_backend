package com.jannchie.biliob.service;

import com.jannchie.biliob.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User createUser(User user);
}
