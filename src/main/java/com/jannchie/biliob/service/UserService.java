package com.jannchie.biliob.service;

import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.User;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
public interface UserService {

    User createUser(User user);

    String getPassword(String name);

    String getRole(String username);

    User getUserInfo();

    User addFavoriteAuthor(@Valid Long mid) throws UserAlreadyFavoriteAuthorException;

    User addFavoriteVideo(@Valid Long aid) throws UserAlreadyFavoriteVideoException;
}
