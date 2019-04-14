package com.jannchie.biliob.controller;

import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.model.Question;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/** @author jannchie */
@RestController
public class UserController {
  private static final Logger logger = LogManager.getLogger(UserController.class);
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user")
  public ResponseEntity createUser(@RequestBody @Valid Map<String, String> requestMap) {
    return userService.createUser(requestMap.get("name"), requestMap.get("password"));
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/author")
  public ResponseEntity addFavoriteAuthor(@RequestBody @Valid Long mid)
      throws UserAlreadyFavoriteAuthorException {
    return userService.addFavoriteAuthor(mid);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/video")
  public ResponseEntity addFavoriteVideo(@RequestBody @Valid Long aid)
      throws UserAlreadyFavoriteVideoException {
    return userService.addFavoriteVideo(aid);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user")
  public ResponseEntity getUserInfo() {
    return userService.getUserInfo();
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

  @RequestMapping(
    method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST},
    value = "/api/no-login"
  )
  public ResponseEntity<Message> noLogin() {

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    return new ResponseEntity<>(new Message(403, "未登录"), headers, HttpStatus.FORBIDDEN);
  }

  @RequestMapping(
    method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST},
    value = "/api/no-rule"
  )
  public ResponseEntity<Message> noRule() {
    return new ResponseEntity<>(new Message(403, "未授权"), HttpStatus.FORBIDDEN);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/login")
  public ResponseEntity login(@RequestBody @Valid User user) {
    return userService.login(user);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/video/{aid}")
  public ResponseEntity deleteFavoriteVideo(@PathVariable("aid") @Valid Long aid) {
    return userService.deleteFavoriteVideoByAid(aid);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/author/{mid}")
  public ResponseEntity deleteFavoriteAuthor(@PathVariable("mid") @Valid Long mid) {
    return userService.deleteFavoriteAuthorByMid(mid);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/check-in")
  public ResponseEntity postCheckIn() {
    return userService.postCheckIn();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/check-in")
  public ResponseEntity getCheckIn() {
    return userService.getCheckIn();
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/status")
  public ResponseEntity forceFocus(
      @RequestParam(defaultValue = "false") @Valid Boolean forceFocus,
      @PathVariable("mid") @Valid Integer mid) {
    return userService.forceFocus(mid, forceFocus);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/question")
  public ResponseEntity postQuestion(@RequestBody @Valid Question question) {
    return userService.postQuestion(question.getQuestion());
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/data")
  public ResponseEntity refreshAuthor(@PathVariable("mid") @Valid Long mid) {
    return userService.refreshAuthor(mid);
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/{aid}/data")
  public ResponseEntity refreshVideo(@PathVariable("aid") @Valid Long aid) {
    return userService.refreshVideo(aid);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/rank/user")
  public ResponseEntity userRank(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer pagesize) {
    return new ResponseEntity<>(userService.sliceUserRank(page, pagesize), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/record")
  public ResponseEntity userRecord() {
    return new ResponseEntity<>(userService.getUserAllRecord(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/{aid}/danmaku")
  public ResponseEntity danmakuAggregate(@PathVariable("aid") @Valid Long aid) {
    return userService.danmakuAggregate(aid);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/video/{aid}/data")
  public ResponseEntity videoObserveAlterFrequency(
      @PathVariable("aid") @Valid Long aid,
      @RequestParam(defaultValue = "1") @Valid Integer timeDurationFlag) {
    return userService.videoObserveAlterFrequency(aid, timeDurationFlag);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/author/{mid}/data")
  public ResponseEntity authorObserveAlterFrequency(
      @PathVariable("mid") @Valid Long mid,
      @RequestParam(defaultValue = "1") @Valid Integer typeFlag) {
    return userService.authorObserveAlterFrequency(mid, typeFlag);
  }
}
