package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.ResultEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author jannchie
 */
@Service
public class AuthorServiceImpl implements AuthorService {
	private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);

	private final AuthorRepository respository;
	private UserService userService;
	private final MongoTemplate mongoTemplate;


  @Autowired
	public AuthorServiceImpl(AuthorRepository respository, UserService userService, MongoTemplate mongoTemplate) {
		this.respository = respository;
		this.userService = userService;
    this.mongoTemplate = mongoTemplate;
  }

	@Override
	public Author getAuthorDetails(Long mid) {
		return respository.findByMid(mid);
	}

	@Override
	public void postAuthorByMid(Long mid)
			throws AuthorAlreadyFocusedException, UserAlreadyFavoriteAuthorException {
		User user = userService.addFavoriteAuthor(mid);
		logger.info(mid);
		logger.info(user.getName());
		if (respository.findByMid(mid) != null) {
			throw new AuthorAlreadyFocusedException(mid);
		}
		respository.save(new Author(mid));
	}

	@Override
	public Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize) {
		if (!(mid == -1)) {
			logger.info(mid);
			return respository.searchByMid(
					mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
		} else if (!Objects.equals(text, "")) {
			logger.info(text);
			return respository.search(
					text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
		} else {
			logger.info("查看所有UP主列表");
			return respository.findAllByDataIsNotNull(
					PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.fans")));
		}
	}

  /**
   * Force Focus a Author or Not.
   *
   *
   * @param mid author id
   * @param forceFocus force focus status
   * @return Force observation or cancel the force observation feedback.
   */
  @Override
  public ResponseEntity forceFocus(Integer mid, @Valid Boolean forceFocus) {
    logger.info("设置{}强制追踪状态为{}",mid,forceFocus);
    mongoTemplate.updateFirst(query(where("mid").is(mid)),update("forceFocus",forceFocus),Author.class);
    return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
  }

  /**
   * get a list of author's fans increase rate.
   *
   * @return list of author rate of fans increase.
   */
  @Override
  public ResponseEntity listFansIncreaseRate() {
    Slice slice = respository.listTopIncreaseRate(PageRequest.of(0,20,new Sort(Sort.Direction.DESC,"cRate")));
    logger.info("获得涨粉榜");
    return new ResponseEntity<>(slice,HttpStatus.OK);
  }

  /**
   * get a list of author's fans decrease rate.
   *
   * @return list of author rate of fans decrease.
   */
  @Override
  public ResponseEntity listFansDecreaseRate() {
    Slice slice = respository.listTopIncreaseRate(PageRequest.of(0,20,new Sort(Sort.Direction.ASC,"cRate")));
    logger.info("获得掉粉榜");
    return new ResponseEntity<>(slice,HttpStatus.OK);
  }

  /**
   * get specific author's fans rate
   *
   * @param mid author id
   * @return list of fans
   */
  @Override
  public ResponseEntity listFansRate(Long mid) {
    Author author = respository.getFansRate(mid);
    List data = author.getFansRate();
    logger.info("获得粉丝变动率");
    return new ResponseEntity<>(data,HttpStatus.OK);
  }
}
