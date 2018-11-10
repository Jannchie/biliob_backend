package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.LoginCheck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jannchie
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final VideoRepository videoRepository;

    private final AuthorRepository authorRepository;

    public UserServiceImpl(UserRepository userRepository, VideoRepository videoRepository, AuthorRepository authorRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public User createUser(User user) throws UserAlreadyExistException {
        if (1 == userRepository.countByName(user.getName())) {
            // 已存在同名
            throw new UserAlreadyExistException(user.getName());
        }

        user.setPassword(new Md5Hash(user.getPassword(), user.getName()).toHex());
        userRepository.save(user);
        logger.info(user.getName());
        return user;
    }

    @Override
    public String getPassword(String name) throws UserNotExistException {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new UserNotExistException(name);
        }
        return userRepository.findByName(name).getPassword();
    }

    @Override
    public String getRole(String name) {
        return userRepository.findByName(name).getRole();
    }

    @Override
    public User getUserInfo() {
        User user = LoginCheck.checkInfo(userRepository);
        logger.info(user.getName());
        return user;
    }

    @Override
    public User addFavoriteAuthor(@Valid Long mid) throws UserAlreadyFavoriteAuthorException {
        User user = LoginCheck.check(userRepository);
        ArrayList<Long> temp = new ArrayList<>();
        if (user.getFavoriteMid() != null) {
            temp = user.getFavoriteMid();
        }
        if (temp.contains(mid)) {
            throw new UserAlreadyFavoriteAuthorException(mid);
        }
        temp.add(mid);
        user.setFavoriteMid(new ArrayList<>(temp));
        userRepository.save(user);
        logger.info(mid);
        logger.info(user.getName());
        return user;
    }


    @Override
    public User addFavoriteVideo(@Valid Long aid) throws UserAlreadyFavoriteVideoException {
        User user = LoginCheck.check(userRepository);
        ArrayList<Long> temp = new ArrayList<>();
        if (user.getFavoriteAid() != null) {
            temp = user.getFavoriteAid();
        }
        if (temp.contains(aid)) {
            throw new UserAlreadyFavoriteVideoException(aid);
        }
        temp.add(aid);
        user.setFavoriteAid(new ArrayList<>(temp));
        userRepository.save(user);
        logger.info(aid);
        logger.info(user.getName());
        return user;
    }

    /**
     * Get user's favorite video page
     *
     * @param page     page number
     * @param pageSize page size
     * @return favorite video page
     */
    @Override
    public Slice getFavoriteVideo(Integer page, Integer pageSize) {
        User user = LoginCheck.check(userRepository);
        ArrayList<Long> aids = user.getFavoriteAid();
        ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
        for (Long aid : aids) {
            HashMap<String, Long> temp = new HashMap<>(1);
            temp.put("aid", aid);
            mapsList.add(temp);
        }
        return videoRepository.getFavoriteVideo(mapsList, PageRequest.of(page, pageSize));
    }

    /**
     * Get user's favorite author page
     *
     * @param page     page number
     * @param pageSize page size
     * @return favorite author page
     */
    @Override
    public Slice getFavoriteAuthor(Integer page, Integer pageSize) {
        User user = LoginCheck.check(userRepository);
        ArrayList<Long> mids = user.getFavoriteAid();
        ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
        for (Long mid : mids) {
            HashMap<String, Long> temp = new HashMap<>(1);
            temp.put("mid", mid);
            mapsList.add(temp);
        }
        return authorRepository.getFavoriteAuthor(mapsList, PageRequest.of(page, pageSize));
    }
}
