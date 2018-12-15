package com.jannchie.biliob.controller;

import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.service.impl.VideoServiceImpl;
import com.jannchie.biliob.utils.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/** @author jannchie */
@RestController
public class UserController {
  private final UserService userService;
  private Map<String,Integer> blackIP = new HashMap();
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }
  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  @RequestMapping(method = RequestMethod.POST, value = "/api/user")
  public ResponseEntity createUser(HttpServletRequest request, @RequestBody @Valid User user)
      throws UserAlreadyExistException {
    String ip = request.getRemoteAddr();

    if (blackIP.containsKey(ip)){
      if (blackIP.get(ip) == -1){
        return new ResponseEntity<>("cheating", HttpStatus.FORBIDDEN);
      }
      Integer newCount = blackIP.get(ip)+1;
      blackIP.put(ip,newCount);
      if (newCount > 20){
        blackIP.put(ip,-1);
        logger.info(ip);
        String ip2 = request.getRemoteUser();
        String ip3 = request.getRemoteHost();
        String ip4 = request.getRemoteAddr();
        System.out.println(ip);
        System.out.println(ip2);
        System.out.println(ip3);
        System.out.println(ip4);
        return new ResponseEntity<>("cheating", HttpStatus.FORBIDDEN);
      }
    }else{
      blackIP.put(ip,0);
    }
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
  public ResponseEntity login(@RequestBody @Valid User user) {
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
