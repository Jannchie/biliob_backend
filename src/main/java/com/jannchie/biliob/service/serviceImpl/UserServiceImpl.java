package com.jannchie.biliob.service.serviceImpl;

import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (1 == userRepository.countByName(user.getName())) {
            throw new UserAlreadyExistException(user.getName());
        }
        userRepository.save(user);
        logger.info(user.getName());
        return user;
    }
}
