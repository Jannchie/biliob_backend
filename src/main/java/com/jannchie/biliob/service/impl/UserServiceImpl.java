package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.FieldConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.Question;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.repository.*;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.DataReducer;
import com.jannchie.biliob.utils.LoginChecker;
import com.jannchie.biliob.utils.MySlice;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.credit.*;
import com.mongodb.BasicDBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

import static com.jannchie.biliob.constant.PageSizeEnum.BIG_SIZE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/** @author jannchie */
@Service
class UserServiceImpl implements UserService {
  private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

  private final CreditUtil creditUtil;

  private final UserRepository userRepository;

  private final VideoRepository videoRepository;

  private final AuthorRepository authorRepository;

  private final UserRecordRepository userRecordRepository;

  private final QuestionRepository questionRepository;

  private final MongoTemplate mongoTemplate;

  private final RefreshAuthorCreditCalculator refreshAuthorCreditCalculator;

  private final RefreshVideoCreditCalculator refreshVideoCreditCalculator;

  private final DanmakuAggregateCreditCalculator danmakuAggregateCreditCalculator;

  private final CheckInCreditCalculator checkInCreditCalculator;

  @Autowired
  public UserServiceImpl(
      CreditUtil creditUtil,
      UserRepository userRepository,
      VideoRepository videoRepository,
      AuthorRepository authorRepository,
      QuestionRepository questionRepository,
      UserRecordRepository userRecordRepository,
      MongoTemplate mongoTemplate,
      RefreshAuthorCreditCalculator refreshAuthorCreditCalculator,
      RefreshVideoCreditCalculator refreshVideoCreditCalculator,
      DanmakuAggregateCreditCalculator danmakuAggregateCreditCalculator,
      CheckInCreditCalculator checkInCreditCalculator) {
    this.creditUtil = creditUtil;
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
    this.authorRepository = authorRepository;
    this.questionRepository = questionRepository;
    this.userRecordRepository = userRecordRepository;
    this.mongoTemplate = mongoTemplate;
    this.refreshAuthorCreditCalculator = refreshAuthorCreditCalculator;
    this.refreshVideoCreditCalculator = refreshVideoCreditCalculator;
    this.danmakuAggregateCreditCalculator = danmakuAggregateCreditCalculator;
    this.checkInCreditCalculator = checkInCreditCalculator;
  }

  @Override
  public ResponseEntity createUser(String username, String password) {
    User user = new User(username, password, RoleEnum.NORMAL_USER.getName());
    if (1 == userRepository.countByName(user.getName())) {
      // 已存在同名
      return new ResponseEntity<>(
          new Result(ResultEnum.USER_ALREADY_EXIST), HttpStatus.BAD_REQUEST);
    }
    user.setPassword(new Md5Hash(user.getPassword(), user.getName()).toHex());
    userRepository.save(user);
    UserServiceImpl.logger.info(user.getName());
    // 不要返回密码
    user.setPassword(null);
    return new ResponseEntity<>(user, HttpStatus.OK);
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
    User user = LoginChecker.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    UserServiceImpl.logger.info(user.getName());
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Override
  public ResponseEntity addFavoriteAuthor(@Valid Long mid)
      throws UserAlreadyFavoriteAuthorException {
    User user = LoginChecker.check();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> temp = new ArrayList<>();
    if (user.getFavoriteMid() != null) {
      temp = user.getFavoriteMid();
    }
    if (temp.contains(mid)) {
      UserServiceImpl.logger.warn("用户：{} 试图重复关注{}", user.getName(), mid);
      return new ResponseEntity<>(
          new Result(ResultEnum.ALREADY_FAVORITE_AUTHOR), HttpStatus.ACCEPTED);
    }
    temp.add(mid);
    user.setFavoriteMid(new ArrayList<>(temp));
    userRepository.save(user);
    UserServiceImpl.logger.info("用户：{} 关注了{}", user.getName(), mid);
    return new ResponseEntity<>(new Result(ResultEnum.ADD_FAVORITE_AUTHOR_SUCCEED), HttpStatus.OK);
  }

  @Override
  public ResponseEntity addFavoriteVideo(@Valid Long aid) throws UserAlreadyFavoriteVideoException {
    User user = LoginChecker.check();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    ArrayList<Long> temp = new ArrayList<>();
    if (user.getFavoriteAid() != null) {
      temp = user.getFavoriteAid();
    }
    if (temp.contains(aid)) {
      UserServiceImpl.logger.warn("用户：{} 试图重复收藏{}", user.getName(), aid);
      return new ResponseEntity<>(
          new Result(ResultEnum.ALREADY_FAVORITE_VIDEO), HttpStatus.ACCEPTED);
    }
    temp.add(aid);
    user.setFavoriteAid(new ArrayList<>(temp));
    userRepository.save(user);
    UserServiceImpl.logger.info("用户：{} 关注了{}", user.getName(), aid);
    return new ResponseEntity<>(new Result(ResultEnum.ADD_FAVORITE_VIDEO_SUCCEED), HttpStatus.OK);
  }

  /**
   * Get user's favorite video page
   *
   * @param page page number
   * @param pageSize page size
   * @return favorite video page
   */
  @Override
  public Slice getFavoriteVideo(Integer page, Integer pageSize) {
    if (pageSize > BIG_SIZE.getValue()) {
      pageSize = BIG_SIZE.getValue();
    }
    User user = LoginChecker.checkInfo();
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
    UserServiceImpl.logger.info(user.getName());
    return videoRepository.getFavoriteVideo(mapsList, PageRequest.of(page, pageSize));
  }

  /**
   * Get user's favorite author page
   *
   * @param page page number
   * @param pageSize page size
   * @return favorite author page
   */
  @Override
  public Slice getFavoriteAuthor(Integer page, Integer pageSize) {
    if (pageSize > BIG_SIZE.getValue()) {
      pageSize = BIG_SIZE.getValue();
    }
    User user = LoginChecker.checkInfo();
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
    UserServiceImpl.logger.info(user.getName());
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
    User user = LoginChecker.check();
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
        UserServiceImpl.logger.info("删除{}关注的UP主：{}", user.getName(), mid);
        return new ResponseEntity<>(new Result(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
      }
    }
    UserServiceImpl.logger.warn("用户：{} 试图删除一个不存在的UP主", user.getName());
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
    User user = LoginChecker.check();
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
        UserServiceImpl.logger.info("用户：{} 删除了收藏的视频，aid：{}", user.getName(), aid);
        return new ResponseEntity<>(new Result(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
      }
    }
    UserServiceImpl.logger.warn("用户：{} 试图删除一个不存在的视频", user.getName());
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
    if (tempUser == null) {
      return new ResponseEntity<>(new Result(ResultEnum.LOGIN_FAILED), HttpStatus.UNAUTHORIZED);
    }

    if (tempUser.getPassword() == null) {
      tempUser.setPassword(encodedPassword);
      userRepository.save(tempUser);
    }

    UsernamePasswordToken token = new UsernamePasswordToken(inputName, encodedPassword);
    token.setRememberMe(true);
    subject.login(token);
    String role = getRole(inputName);
    UserServiceImpl.logger.info("{}：{} 登录成功", role, inputName);
    return new ResponseEntity<>(new Result(ResultEnum.LOGIN_SUCCEED, getUserInfo()), HttpStatus.OK);
  }

  /**
   * user can check in and get credit every eight hour.
   *
   * @return check in response
   */
  @Override
  public ResponseEntity postCheckIn() {
    return checkInCreditCalculator.executeAndGetResponse(CreditConstant.CHECK_IN);
  }

  private ResponseEntity<Result> getResponseForCredit(User user, ResultEnum resultEnum) {
    Integer credit;
    HashMap<String, Integer> data = creditUtil.calculateCredit(user, CreditConstant.CHECK_IN);
    if (data.get(FieldConstant.CREDIT.getValue()) != -1) {
      UserServiceImpl.logger.warn(
          "用户：{}，因{}发生积分变动，当前积分：{}", user.getName(), resultEnum.getMsg(), data);
      return new ResponseEntity<>(new Result(resultEnum, data), HttpStatus.OK);
    } else {
      UserServiceImpl.logger.warn("用户：{}，因积分不足，扣分失败", user.getName());
      return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
    }
  }

  /**
   * to know whether user is checked in
   *
   * @return check in status
   */
  @Override
  public ResponseEntity getCheckIn() {
    User user = LoginChecker.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    Boolean isCheckedIn =
        mongoTemplate.exists(new Query(where("name").is(user.getName())), "check_in");
    HashMap<String, Boolean> statusHashMap = new HashMap<>(1);
    statusHashMap.put("status", isCheckedIn);
    UserServiceImpl.logger.info("用户：{},签到状态为{}", user.getName(), isCheckedIn);
    return new ResponseEntity<>(statusHashMap, HttpStatus.OK);
  }

  /**
   * Force Focus a Author or Not.
   *
   * @param mid author id
   * @param forceFocus force focus status
   * @return Force observation or cancel the force observation feedback.
   */
  @Override
  public ResponseEntity forceFocus(Integer mid, @Valid Boolean forceFocus) {

    User user = LoginChecker.checkInfo();

    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }

    if (!forceFocus) {
      // only admin can set force focus to false
      if (Objects.equals(user.getRole(), RoleEnum.NORMAL_USER.getName())) {
        mongoTemplate.updateFirst(
            query(where("mid").is(mid)), update("forceFocus", forceFocus), Author.class);
        UserServiceImpl.logger.info("用户：{}设置{}强制追踪状态为{}", user.getName(), mid, forceFocus);
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

    HashMap<String, Integer> data =
        creditUtil.calculateCredit(user, CreditConstant.SET_FORCE_OBSERVE);

    if (data.get(FieldConstant.CREDIT.getValue()) != -1) {
      mongoTemplate.updateFirst(
          query(where("mid").is(mid)), update("forceFocus", true), Author.class);
      UserServiceImpl.logger.info("用户：{} 设置 {} 强制追踪状态为{}", user.getName(), mid, true);
      return new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
    }
  }

  /**
   * post a question
   *
   * @param question the question text
   * @return the post result.
   */
  @Override
  public ResponseEntity postQuestion(String question) {
    User user = LoginChecker.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    String userName = user.getName();
    HashMap<String, Integer> data = creditUtil.calculateCredit(user, CreditConstant.ASK_QUESTION);
    if (data.get(FieldConstant.CREDIT.getValue()) != -1) {
      questionRepository.save(new Question(question, userName));
      UserServiceImpl.logger.info("用户：{} 提出了一个问题：{}", user.getName(), question);
      return new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
    }
  }

  /**
   * Refresh author data immediately.
   *
   * @param mid author id
   * @return response
   */
  @Override
  public ResponseEntity refreshAuthor(@Valid Long mid) {
    return refreshAuthorCreditCalculator.executeAndGetResponse(
        CreditConstant.REFRESH_AUTHOR_DATA, mid);
  }

  @Override
  public ResponseEntity refreshVideo(@Valid Long aid) {
    return refreshVideoCreditCalculator.executeAndGetResponse(
        CreditConstant.REFRESH_VIDEO_DATA, aid);
  }

  /**
   * Rank of user, order by exp
   *
   * @param page offset
   * @param pagesize number of element
   * @return the slice of user rank
   */
  @Override
  public MySlice<User> sliceUserRank(Integer page, Integer pagesize) {
    // max size is 20
    if (pagesize > BIG_SIZE.getValue()) {
      pagesize = BIG_SIZE.getValue();
    }
    // get user slice
    Slice<User> s =
        userRepository.findTopUserByOrderByExp(
            PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "exp")));
    return new MySlice<>(s);
  }

  /**
   * User starts a danmaku aggregate task.
   *
   * @param aid the video id being aggregated
   * @return the response
   */
  @Override
  public ResponseEntity danmakuAggregate(@Valid Long aid) {
    return danmakuAggregateCreditCalculator.executeAndGetResponse(
        CreditConstant.DANMAKU_AGGREGATE, aid);
  }

  /**
   * slice the user record
   *
   * @param page page number
   * @param pagesize page size
   * @return the slice of user record
   */
  @Override
  public MySlice<UserRecord> sliceUserRecord(Integer page, Integer pagesize) {
    pagesize = DataReducer.limitPagesize(pagesize);
    User user = LoginChecker.checkInfo();
    if (user != null) {
      String userName = user.getName();
      Slice<UserRecord> slice =
          userRecordRepository.findByUserNameOrderByDatetimeDesc(
              userName, PageRequest.of(page, pagesize));
      return new MySlice<>(slice);
    } else {
      return null;
    }
  }

  /**
   * Get user's all records.
   *
   * @return user record array list
   */
  @Override
  public ArrayList<UserRecord> getUserAllRecord() {
    User user = LoginChecker.checkInfo();
    if (user != null) {
      String userName = user.getName();
      return userRecordRepository.findAllByUserNameOrderByDatetimeDesc(userName);
    } else {
      return null;
    }
  }

  /**
   * video observe frequency alter
   *
   * @param aid video id
   * @param typeFlag type flag
   * @return operation result
   */
  @Override
  public ResponseEntity videoObserveAlterFrequency(@Valid Long aid, @Valid Integer typeFlag) {
    return null;
  }

  /**
   * author observe frequency alter
   *
   * @param mid video id
   * @param typeFlag type flag
   * @return operation result
   */
  @Override
  public ResponseEntity authorObserveAlterFrequency(@Valid Long mid, @Valid Integer typeFlag) {
    return null;
  }
}
