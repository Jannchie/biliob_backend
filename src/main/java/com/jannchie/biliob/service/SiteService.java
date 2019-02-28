package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface SiteService {

  /**
   * Get the data of the number of people watching video on bilibili.
   *
   * @param days The days of data that this method should return.
   * @return Online number.
   */
  ResponseEntity listOnline(Integer days);
}
