package com.jannchie.biliob.service;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface AuthorService {

  /**
   * 获取作者详情
   *
   * @param mid 作者id
   * @return 作者详细信息
   */
  Author getAuthorDetails(Long mid);

  /**
   * 添加作者追踪
   *
   * @param mid 作者id
   * @throws UserAlreadyFavoriteAuthorException 用户已经观测该作者
   * @throws AuthorAlreadyFocusedException 作者已经在系统中
   */
  void postAuthorByMid(Long mid)
      throws UserAlreadyFavoriteAuthorException, AuthorAlreadyFocusedException;

  /**
   * 获取作者页
   *
   * @param mid 作者id
   * @param text 文本
   * @param page 页数
   * @param pagesize 页大小
   * @param sort sort field
   * @return 作者页
   */
  MySlice<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize, Integer sort);

  /**
   * get a list of author's fans increase rate.
   *
   * @return list of author rate of fans increase.
   */
  ResponseEntity listFansIncreaseRate();

  /**
   * get a list of author's fans decrease rate.
   *
   * @return list of author rate of fans decrease.
   */
  ResponseEntity listFansDecreaseRate();

  /**
   * get specific author's fans rate
   *
   * @param mid author id
   * @return list of fans
   */
  ResponseEntity listFansRate(Long mid);

  /**
   * get author information exclude history data.
   *
   * @param mid author id
   * @return author
   */
  Author getAuthorInfo(Long mid);

  /**
   * list real time data
   *
   * @param aMid one author id
   * @param bMid another author id
   * @return Real time fans responseEntity
   */
  ResponseEntity getRealTimeData(Long aMid, Long bMid);

  /**
   * Get the number of author be observed.
   *
   * @return the number of author be observed.
   */
  Long getNumberOfAuthor();
}
