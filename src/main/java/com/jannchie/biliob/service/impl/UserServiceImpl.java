package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.exception.UserAlreadyExistException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.CheckIn;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.CreditUtil;
import com.jannchie.biliob.utils.LoginCheck;
import com.jannchie.biliob.utils.Result;
import com.mongodb.BasicDBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.jannchie.biliob.constant.RoleEnum.NORMAL_USER;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author jannchie
 */
@Service
class UserServiceImpl implements UserService {
  private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

  private final CreditUtil creditUtil;

  private final UserRepository userRepository;

  private final VideoRepository videoRepository;

  private final AuthorRepository authorRepository;

  private final MongoTemplate mongoTemplate;

  private UserServiceImpl(
      CreditUtil creditUtil,
      UserRepository userRepository,
      VideoRepository videoRepository,
      AuthorRepository authorRepository,
      MongoTemplate mongoTemplate) {
    this.creditUtil = creditUtil;
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
    this.authorRepository = authorRepository;
    this.mongoTemplate = mongoTemplate;
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
  public ResponseEntity getUserInfo() {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    logger.info(user.getName());
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Override
  public ResponseEntity addFavoriteAuthor(@Valid Long mid)
      throws UserAlreadyFavoriteAuthorException {
    User user = LoginCheck.check();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> temp = new ArrayList<>();
    if (user.getFavoriteMid() != null) {
      temp = user.getFavoriteMid();
    }
    if (temp.contains(mid)) {
      logger.warn("用户：{} 试图重复关注{}", user.getName(), mid);
      return new ResponseEntity<>(
          new Result(ResultEnum.ALREADY_FAVORITE_AUTHOR), HttpStatus.ACCEPTED);
    }
    temp.add(mid);
    user.setFavoriteMid(new ArrayList<>(temp));
    userRepository.save(user);
    logger.info("用户：{} 关注了{}", user.getName(), mid);
    return new ResponseEntity<>(new Result(ResultEnum.ADD_FAVORITE_AUTHOR_SUCCEED), HttpStatus.OK);
  }

  @Override
  public ResponseEntity addFavoriteVideo(@Valid Long aid) throws UserAlreadyFavoriteVideoException {
    User user = LoginCheck.check();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> temp = new ArrayList<>();
    if (user.getFavoriteAid() != null) {
      temp = user.getFavoriteAid();
    }
    if (temp.contains(aid)) {
      logger.warn("用户：{} 试图重复收藏{}", user.getName(), aid);
      return new ResponseEntity<>(
          new Result(ResultEnum.ALREADY_FAVORITE_VIDEO), HttpStatus.ACCEPTED);
    }
    temp.add(aid);
    user.setFavoriteAid(new ArrayList<>(temp));
    userRepository.save(user);
    logger.info("用户：{} 关注了{}", user.getName(), aid);
    return new ResponseEntity<>(new Result(ResultEnum.ADD_FAVORITE_VIDEO_SUCCEED), HttpStatus.OK);
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
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return null;
    }
    if (user.getFavoriteAid() == null) {
      return null;
    }
    ArrayList<Long> aids = user.getFavoriteAid();
    ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
    for (Long aid : aids) {
      HashMap<String, Long> temp = new HashMap<>(1);
      temp.put("aid", aid);
      mapsList.add(temp);
    }
    logger.info(user.getName());
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
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return null;
    }
    if (user.getFavoriteMid() == null) {
      return null;
    }
    ArrayList<Long> mids = user.getFavoriteMid();
    ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
    for (Long mid : mids) {
      HashMap<String, Long> temp = new HashMap<>(1);
      temp.put("mid", mid);
      mapsList.add(temp);
    }
    logger.info(user.getName());
    return authorRepository.getFavoriteAuthor(mapsList, PageRequest.of(page, pageSize));
  }

  /**
   * delete user's favorite author by author id
   *
   * @param mid author's id
   * @return response with message
   */
  @Override
  public ResponseEntity deleteFavoriteAuthorByMid(Long mid) {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> mids = user.getFavoriteMid();
    for (int i = 0; i < mids.size(); i++) {
      if (Objects.equals(mids.get(i), mid)) {
        mids.remove(i);
        user.setFavoriteMid(mids);
        userRepository.save(user);
        logger.info("删除{}关注的UP主：{}", user.getName(), mid);
        return new ResponseEntity<>(new Result(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
      }
    }
    logger.warn("用户：{} 试图删除一个不存在的UP主", user.getName());
    return new ResponseEntity<>(new Result(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.NOT_FOUND);
  }

  /**
   * delete user's favorite video by video id
   *
   * @param aid video's id
   * @return response with message
   */
  @Override
  public ResponseEntity deleteFavoriteVideoByAid(Long aid) {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> aids = user.getFavoriteAid();
    for (int i = 0; i < aids.size(); i++) {
      if (Objects.equals(aids.get(i), aid)) {
        aids.remove(i);
        user.setFavoriteAid(aids);
        userRepository.save(user);
        logger.info("用户：{} 删除了收藏的视频，aid：{}", user.getName(), aid);
        return new ResponseEntity<>(new Result(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
      }
    }
    logger.warn("用户：{} 试图删除一个不存在的视频", user.getName());
    return new ResponseEntity<>(new Result(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.NOT_FOUND);
  }

  /**
   * login
   *
   * @param user user information
   * @return login information
   */
  @Override
  public ResponseEntity login(User user) {
    String inputName = user.getName();
    String inputPassword = user.getPassword();
    String encodedPassword = new Md5Hash(inputPassword, inputName).toHex();
    Subject subject = SecurityUtils.getSubject();

    User tempUser = userRepository.findByName(inputName);
    if (tempUser.getPassword() == null){
      tempUser.setPassword(encodedPassword);
      userRepository.save(tempUser);
    }


    UsernamePasswordToken token = new UsernamePasswordToken(inputName, encodedPassword);
    token.setRememberMe(true);
    subject.login(token);
    String role = getRole(inputName);
    if (NORMAL_USER.getName().equals(role)) {
      logger.info("普通用户：{} 登录成功", inputName);
      return new ResponseEntity<>(
          new Result(ResultEnum.LOGIN_SUCCEED, getUserInfo()), HttpStatus.OK);
    }
    return new ResponseEntity<>(new Result(ResultEnum.LOGIN_FAILED), HttpStatus.UNAUTHORIZED);
  }

  /**
   * user can check in and get credit every eight hour.
   *
   * @return check in response
   */
  @Override
  public ResponseEntity postCheckIn() {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    Boolean isCheckedIn =
        mongoTemplate.exists(new Query(where("name").is(user.getName())), "check_in");
    String userName = user.getName();
    Integer credit = user.getCredit();
    if (isCheckedIn) {
      logger.warn("用户：{},试图重复签到,当前积分：{}", userName, credit);
      return new ResponseEntity<>(new Result(ResultEnum.ALREADY_SIGNED), HttpStatus.ACCEPTED);
    } else {

      // 插入已签到集合
      mongoTemplate.insert(new CheckIn(userName), "check_in");

      Boolean isCreditEnough = creditUtil.calculateCredit(user, CreditConstant.CHECK_IN);
      if (isCreditEnough) {
        return new ResponseEntity<>(new Result(ResultEnum.SIGN_SUCCEED), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
      }
    }
  }

  /**
   * to know whether user is checked in
   *
   * @return check in status
   */
  @Override
  public ResponseEntity getCheckIn() {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    Boolean isCheckedIn =
        mongoTemplate.exists(new Query(where("name").is(user.getName())), "check_in");
    HashMap<String, Boolean> statusHashMap = new HashMap<>(1);
    statusHashMap.put("status", isCheckedIn);
    logger.info("用户：{},签到状态为{}", user.getName(), isCheckedIn);
    return new ResponseEntity<>(statusHashMap, HttpStatus.OK);
  }

  /**
   * Force Focus a Author or Not.
   *
   * @param mid        author idW
   * @param forceFocus force focus status
   * @return Force observation or cancel the force observation feedback.
   */
  @Override
  public ResponseEntity forceFocus(Integer mid, @Valid Boolean forceFocus) {

    User user = LoginCheck.checkInfo();

    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }

    if (!forceFocus) {
      // only admin can set force focus to false
      if (Objects.equals(user.getRole(), RoleEnum.ADMIN.getName())) {
        mongoTemplate.updateFirst(
            query(where("mid").is(mid)), update("forceFocus", forceFocus), Author.class);
        logger.info("用户：{}设置{}强制追踪状态为{}", user.getName(), mid, forceFocus);
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(
            new Result(ResultEnum.PERMISSION_DENIED), HttpStatus.UNAUTHORIZED);
      }
    }
    BasicDBObject fieldsObject = new BasicDBObject();
    BasicDBObject dbObject = new BasicDBObject();
    dbObject.put("mid", mid);
    fieldsObject.put("forceFocus", true);
    Author author =
        mongoTemplate.findOne(
            new BasicQuery(dbObject.toJson(), fieldsObject.toJson()), Author.class);

    if (author == null) {
      return new ResponseEntity<>(new Result(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.ACCEPTED);
    } else if (author.getForceFocus() != null && author.getForceFocus()) {
      return new ResponseEntity<>(new Result(ResultEnum.ALREADY_FORCE_FOCUS), HttpStatus.ACCEPTED);
    }

    Boolean isCreditEnough = creditUtil.calculateCredit(user, CreditConstant.SET_FORCE_OBSERVE);
    if (isCreditEnough) {
      mongoTemplate.updateFirst(
          query(where("mid").is(mid)), update("forceFocus", true), Author.class);
      logger.info("用户：{}设置{}强制追踪状态为{}", user.getName(), mid, true);
      return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
    }
  }
}
