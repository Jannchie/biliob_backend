package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Site;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jannchie
 */
@Service
public interface SiteService {

  /**
   * Get the data of the number of people watching video on bilibili.
   *
   * @param days The days of data that this method should return.
   * @return Online number.
   */
  List<Site> getPlayOnline(Integer days);
}
