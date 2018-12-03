package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.UserType;
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
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.ResultEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author jannchie
 */
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

	private final UserRepository userRepository;

	private final VideoRepository videoRepository;

	private final AuthorRepository authorRepository;

	public UserServiceImpl(
			UserRepository userRepository,
			VideoRepository videoRepository,
			AuthorRepository authorRepository) {
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
		User user = LoginCheck.check(userRepository);
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
	public ResponseEntity<Message> deleteFavoriteAuthorByMid(Long mid) {
		User user = LoginCheck.check(userRepository);
		ArrayList<Long> mids = user.getFavoriteMid();
		for (int i = 0; i < mids.size(); i++) {
			if (Objects.equals(mids.get(i), mid)) {
				mids.remove(i);
				user.setFavoriteMid(mids);
				userRepository.save(user);
        logger.info("删除{}关注的UP主：{}", user.getName(), mid);
        return new ResponseEntity<>(new Message(-1, "删除成功"), HttpStatus.OK);
      }
		}
    return new ResponseEntity<>(new Message(-1, "未找到该UP主"), HttpStatus.NOT_FOUND);
  }

	/**
	 * delete user's favorite video by video id
	 *
	 * @param aid video's id
	 * @return response with message
	 */
	@Override
	public ResponseEntity<Message> deleteFavoriteVideoByAid(Long aid) {
		User user = LoginCheck.check(userRepository);
		ArrayList<Long> aids = user.getFavoriteAid();
		for (int i = 0; i < aids.size(); i++) {
			if (Objects.equals(aids.get(i), aid)) {
				aids.remove(i);
				user.setFavoriteAid(aids);
				userRepository.save(user);
				return new ResponseEntity<>(new Message(200, "删除成功"), HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(new Message(404, "未找到该视频"), HttpStatus.NOT_FOUND);
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
		UsernamePasswordToken token = new UsernamePasswordToken(inputName, encodedPassword);
		token.setRememberMe(true);
		subject.login(token);
		String role = getRole(inputName);
		if (UserType.NORMAL_USER.equals(role)) {
      return new ResponseEntity<>(new Result(ResultEnum.LOGIN_SUCCEED, getUserInfo()), HttpStatus.OK);
    }
    return new ResponseEntity<>(new Result(ResultEnum.LOGIN_FAILED), HttpStatus.UNAUTHORIZED);
  }
}
