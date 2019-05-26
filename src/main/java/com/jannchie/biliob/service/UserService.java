package com.jannchie.biliob.service;

import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;

/** @author jannchie */
@Service
public interface UserService {

  /**
   * 创建用户
   *
   * @param userName 用户名
   * @param password 密码
   * @return 创建结果
   */
  ResponseEntity createUser(String userName, String password);

  /**
   * 获取密码
   *
   * @param name 用户名
   * @return 密码
   * @throws UserNotExistException user not exist
   */
  String getPassword(String name) throws UserNotExistException;

  /**
   * 获取角色
   *
   * @param username 用户名
   * @return 角色名
   */
  String getRole(String username);

  /**
   * 获取用户信息
   *
   * @return 用户信息
   */
  ResponseEntity getUserInfo();

  /**
   * 添加作者关注
   *
   * @param mid 作者id
   * @return 用户
   * @throws UserAlreadyFavoriteAuthorException 用户已关注作者异常
   */
  ResponseEntity addFavoriteAuthor(@Valid Long mid) throws UserAlreadyFavoriteAuthorException;

  /**
   * 添加视频关注
   *
   * @param aid 视频id
   * @return 用户
   * @throws UserAlreadyFavoriteVideoException 用户已关注视频异常
   */
  ResponseEntity addFavoriteVideo(@Valid Long aid) throws UserAlreadyFavoriteVideoException;

  /**
   * Get user's favorite video page
   *
   * @param page page number
   * @param pageSize page size
   * @return favorite video page
   */
  Slice getFavoriteVideo(Integer page, Integer pageSize);

  /**
   * Get user's favorite author page
   *
   * @param page page number
   * @param pageSize page size
   * @return favorite author page
   */
  Slice getFavoriteAuthor(Integer page, Integer pageSize);

  /**
   * delete user's favorite author by author id
   *
   * @param mid author's id
   * @return response with message
   */
  ResponseEntity deleteFavoriteAuthorByMid(Long mid);

  /**
   * delete user's favorite video by video id
   *
   * @param aid video's id
   * @return response with message
   */
  ResponseEntity deleteFavoriteVideoByAid(Long aid);

  /**
   * login
   *
   * @param user user information
   * @return login response
   */
  ResponseEntity login(User user);

  /**
   * user can check in and get credit every eight hour.
   *
   * @return check in response
   */
  ResponseEntity postCheckIn();

  /**
   * to know whether user is checked in
   *
   * @return check in status
   */
  ResponseEntity getCheckIn();

  /**
   * Force Focus a Author or Not.
   *
   * @param mid author id
   * @param forceFocus force focus status
   * @return Force observation or cancel the force observation feedback.
   */
  ResponseEntity forceFocus(Long mid, @Valid Boolean forceFocus);

  /**
   * post a question
   *
   * @param question the question text
   * @return the post result.
   */
  ResponseEntity postQuestion(String question);

  /**
   * Refresh author data immediately.
   *
   * @param mid author id
   * @return response
   */
  ResponseEntity refreshAuthor(@Valid Long mid);

  /**
   * Refresh video data immediately.
   *
   * @param aid video id
   * @return response
   */
  ResponseEntity refreshVideo(@Valid Long aid);

  /**
   * Rank of user, order by exp
   *
   * @param page offset
   * @param pagesize number of element
   * @return the slice of user rank
   */
  MySlice<User> sliceUserRank(Integer page, Integer pagesize);

  /**
   * User starts a danmaku aggregate task.
   *
   * @param aid the video id being aggregated
   * @return the response
   */
  ResponseEntity danmakuAggregate(@Valid Long aid);

  /**
   * slice the user record
   *
   * @param page page number
   * @param pagesize page size
   * @return the slice of user record
   */
  MySlice<UserRecord> sliceUserRecord(Integer page, Integer pagesize);

  /**
   * Get user's all records.
   *
   * @return user record array list
   */
  ArrayList<UserRecord> getUserAllRecord();

  /**
   * video observe frequency alter
   *
   * @param aid video id
   * @param typeFlag type flag
   * @return operation result
   */
  ResponseEntity videoObserveAlterFrequency(@Valid Long aid, @Valid Integer typeFlag);

  /**
   * author observe frequency alter
   *
   * @param mid video id
   * @param typeFlag type flag
   * @return operation result
   */
  ResponseEntity authorObserveAlterFrequency(@Valid Long mid, @Valid Integer typeFlag);
}
