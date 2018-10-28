package com.jannchie.biliob.service.serviceImpl;

import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
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

        user.setPassword(new Md5Hash(user.getPassword(), user.getName()).toHex());
        userRepository.save(user);
        logger.info(user.getName());
        return user;
    }

    @Override
    public  String getPassword(String name){
        User user  = userRepository.findByName(name);
        if(user == null) {
            throw new UserNotExistException(name);
        }
        return userRepository.findByName(name).getPassword();
    }
    @Override
    public  String getRole(String name){
        return userRepository.findByName(name).getRole();
    }
}
