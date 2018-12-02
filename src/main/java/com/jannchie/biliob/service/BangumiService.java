package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface BangumiService {

  /**
   * Get the data of bangumi list, including bangumi name, pts and tags.
   *
   * @param page page number
   * @param pagesize page size
   * @return Online number.
   */
  ResponseEntity listBangumi(Integer page, Integer pagesize);
}
