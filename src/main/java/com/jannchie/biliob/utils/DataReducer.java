package com.jannchie.biliob.utils;

import org.springframework.stereotype.Component;

import static com.jannchie.biliob.constant.PageSizeEnum.BIG_SIZE;

/** @author jannchie */
@Component
public class DataReducer {
  public static Integer limitPagesize(Integer pagesize) {
    if ((pagesize <= BIG_SIZE.getValue()) && (pagesize > 0)) {
      return pagesize;
    } else {
      return 0;
    }
  }
}
