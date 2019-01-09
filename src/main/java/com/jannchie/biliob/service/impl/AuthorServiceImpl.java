package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.Result;
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

import java.util.List;
import java.util.Objects;

import static com.jannchie.biliob.constant.ResultEnum.PARAM_ERROR;

/**
 * @author jannchie
 */
@Service
public class AuthorServiceImpl implements AuthorService {
  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);

  private final AuthorRepository respository;
  private final MongoTemplate mongoTemplate;
  private UserService userService;

  @Autowired
  public AuthorServiceImpl(
      AuthorRepository respository, UserService userService, MongoTemplate mongoTemplate) {
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
    userService.addFavoriteAuthor(mid);
    logger.info(mid);
    if (respository.findByMid(mid) != null) {
      throw new AuthorAlreadyFocusedException(mid);
    }
    respository.save(new Author(mid));
  }

  @Override
  public Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize) {
    if(pagesize > PageSizeEnum.BIG_SIZE.getValue()){
      pagesize = PageSizeEnum.BIG_SIZE.getValue();
    }
    if (!(mid == -1)) {
      logger.info(mid);
      return respository.searchByMid(
          mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cFans")));
    } else if (!Objects.equals(text, "")) {
      logger.info(text);
      return respository.search(
          text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cFans")));
    } else {
      logger.info("查看所有UP主列表");
      return respository.findAllByDataIsNotNull(
          PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "cFans")));
    }
  }

  /**
   * get a list of author's fans increase rate.
   *
   * @return list of author rate of fans increase.
   */
  @Override
  public ResponseEntity listFansIncreaseRate() {
    Slice slice =
        respository.listTopIncreaseRate(
            PageRequest.of(0, 20, new Sort(Sort.Direction.DESC, "cRate")));
    logger.info("获得涨粉榜");
    return new ResponseEntity<>(slice, HttpStatus.OK);
  }

  /**
   * get a list of author's fans decrease rate.
   *
   * @return list of author rate of fans decrease.
   */
  @Override
  public ResponseEntity listFansDecreaseRate() {
    Slice slice =
        respository.listTopIncreaseRate(
            PageRequest.of(0, 20, new Sort(Sort.Direction.ASC, "cRate")));
    logger.info("获得掉粉榜");
    return new ResponseEntity<>(slice, HttpStatus.OK);
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
    return new ResponseEntity<>(data, HttpStatus.OK);
  }
}
