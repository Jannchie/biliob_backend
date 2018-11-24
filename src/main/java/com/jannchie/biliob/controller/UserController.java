package com.jannchie.biliob.controller;

import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/** @author jannchie */
@RestController
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user")
  public ResponseEntity<User> createUser(@RequestBody @Valid User user)
      throws UserAlreadyExistException {
    User newUser = userService.createUser(user);
    return new ResponseEntity<>(newUser, HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/author")
  public ResponseEntity<Message> addFavoriteAuthor(@RequestBody @Valid Long mid)
      throws UserAlreadyFavoriteAuthorException {
    userService.addFavoriteAuthor(mid);
    return new ResponseEntity<>(new Message(201, "添加收藏作者成功"), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/video")
  public ResponseEntity<Message> addFavoriteVideo(@RequestBody @Valid Long aid)
      throws UserAlreadyFavoriteVideoException {
    userService.addFavoriteVideo(aid);
    return new ResponseEntity<>(new Message(201, "添加收藏视频成功"), HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user")
  public ResponseEntity<User> createUser() {
    User user = userService.getUserInfo();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/video")
  public Slice getFavoriteVideo(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer pageSize) {
    return userService.getFavoriteVideo(page, pageSize);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/author")
  public Slice getFavoriteAuthor(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer pageSize) {
    return userService.getFavoriteAuthor(page, pageSize);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/no-login")
  public ResponseEntity<Message> noLogin() {
    return new ResponseEntity<>(new Message(403, "未登录"), HttpStatus.FORBIDDEN);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/no-rule")
  public ResponseEntity<Message> noRule() {
    return new ResponseEntity<>(new Message(403, "未授权"), HttpStatus.FORBIDDEN);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/login")
  public ResponseEntity<Message> login(@RequestBody @Valid User user) {
    return userService.login(user);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/video/{aid}")
  public ResponseEntity<Message> deleteFavoriteVideo(@PathVariable("aid") @Valid Long aid) {
    return userService.deleteFavoriteVideoByAid(aid);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/author/{mid}")
  public ResponseEntity<Message> deleteFavoriteAuthor(@PathVariable("mid") @Valid Long mid) {
    return userService.deleteFavoriteAuthorByMid(mid);
  }
}
