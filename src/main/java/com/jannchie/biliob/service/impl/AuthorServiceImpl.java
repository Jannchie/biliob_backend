package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.AuthorSortEnum;
import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.RealTimeFans;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.repository.RealTimeFansRepository;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.SiteService;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.InputInspection;
import com.jannchie.biliob.utils.MySlice;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/** @author jannchie */
@Service
@CacheConfig(cacheNames = "authorService")
public class AuthorServiceImpl implements AuthorService {
  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private final RedisOps redisOps;
  private final AuthorRepository respository;
  private final RealTimeFansRepository realTimeFansRepository;
  private final MongoTemplate mongoTemplate;
  private final UserService userService;
  private final SiteService siteService;

  @Autowired
  public AuthorServiceImpl(
      AuthorRepository respository,
      UserService userService,
      MongoTemplate mongoTemplate,
      InputInspection inputInspection,
      RealTimeFansRepository realTimeFansRepository,
      RedisOps redisOps,
      SiteService siteService) {
    this.respository = respository;
    this.userService = userService;
    this.mongoTemplate = mongoTemplate;
    this.realTimeFansRepository = realTimeFansRepository;
    this.redisOps = redisOps;
    this.siteService = siteService;
  }

  @Override
  public Author getAuthorDetails(Long mid) {
    return respository.findByMid(mid);
  }

  @Override
  public void postAuthorByMid(Long mid)
      throws AuthorAlreadyFocusedException, UserAlreadyFavoriteAuthorException {
    userService.addFavoriteAuthor(mid);
    AuthorServiceImpl.logger.info(mid);
    if (respository.findByMid(mid) != null) {
      throw new AuthorAlreadyFocusedException(mid);
    }
    redisOps.postAuthorCrawlTask(mid);
    respository.save(new Author(mid));
  }

  @Override
  @Cacheable(value = "author_slice", key = "#mid + #text + #page + #pagesize + #sort")
  public MySlice<Author> getAuthor(
      Long mid, String text, Integer page, Integer pagesize, Integer sort) {
    if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
      pagesize = PageSizeEnum.BIG_SIZE.getValue();
    }
    String sortKey = AuthorSortEnum.getKeyByFlag(sort);
    if (!(mid == -1)) {
      AuthorServiceImpl.logger.info(mid);
      return new MySlice<>(
          respository.searchByMid(
              mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
    } else if (!Objects.equals(text, "")) {
      AuthorServiceImpl.logger.info(text);
      if (InputInspection.isId(text)) {
        // get a mid
        return new MySlice<>(
            respository.searchByMid(
                Long.valueOf(text),
                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
      }
      // get text
      String[] textArray = text.split(" ");
      MySlice<Author> mySlice =
          new MySlice<>(
              respository.findByKeywordContaining(
                  textArray,
                  PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
      if (mySlice.getContent().isEmpty()) {
        for (String eachText : textArray) {
          HashMap<String, String> map = new HashMap<>(1);
          map.put("mid", eachText);
          mongoTemplate.insert(map, "search_word");
        }
      }
      return mySlice;
    } else {
      AuthorServiceImpl.logger.info("查看所有UP主列表");
      return new MySlice<>(
          respository.findAllByDataIsNotNull(
              PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
    }
  }

  /**
   * get a list of author's fans increase rate.
   *
   * @return list of author rate of fans increase.
   */
  @Override
  public ResponseEntity listFansIncreaseRate() {
    Slice<Author> slice =
        respository.listTopIncreaseRate(
            PageRequest.of(0, 20, new Sort(Sort.Direction.DESC, "cRate")));
    AuthorServiceImpl.logger.info("获得涨粉榜");
    return new ResponseEntity<>(slice, HttpStatus.OK);
  }

  /**
   * get a list of author's fans decrease rate.
   *
   * @return list of author rate of fans decrease.
   */
  @Override
  public ResponseEntity listFansDecreaseRate() {
    Slice<Author> slice =
        respository.listTopIncreaseRate(
            PageRequest.of(0, 20, new Sort(Sort.Direction.ASC, "cRate")));
    AuthorServiceImpl.logger.info("获得掉粉榜");
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
    AuthorServiceImpl.logger.info("获得粉丝变动率");
    return new ResponseEntity<>(data, HttpStatus.OK);
  }

  /**
   * get author information exclude history data.
   *
   * @param mid author id
   * @return author
   */
  @Override
  public Author getAuthorInfo(Long mid) {
    return respository.findAuthorByMid(mid);
  }

  /**
   * list real time data
   *
   * @param aMid one author id
   * @param bMid another author id
   * @return Real time fans responseEntity
   */
  @Override
  public ResponseEntity getRealTimeData(Long aMid, Long bMid) {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setTimeZone(TimeZone.getTimeZone("GMT+:00:00"));

    List<RealTimeFans> aRealTimeFans = listRealTimeFans(aMid);
    ArrayList<Integer> aFans = new ArrayList<>();
    ArrayList<String> datetime = new ArrayList<>();
    for (RealTimeFans item : aRealTimeFans) {
      c.setTime(item.getDatetime());
      datetime.add(format.format(c.getTime()));
      aFans.add(item.getFans());
    }

    List<RealTimeFans> bRealTimeFans = listRealTimeFans(bMid);
    ArrayList<Integer> bFans = new ArrayList<>();
    for (RealTimeFans item : bRealTimeFans) {
      bFans.add(item.getFans());
    }

    HashMap<String, Cloneable> result = new HashMap<>(3);
    result.put("aFans", aFans);
    result.put("bFans", bFans);
    result.put("datetime", datetime);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private List<RealTimeFans> listRealTimeFans(Long mid) {
    return realTimeFansRepository.findTop180ByMidOrderByDatetimeDesc(mid);
  }
}
