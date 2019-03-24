package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Author;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.jannchie.biliob.constant.PageSizeEnum.BIG_SIZE;

/** @author jannchie */
@Component
public class DataReducer {
  public static Author authorDataDownSampling(Author author) {
    // TODO: author data down sampling
    // author data become very big.
    // It is necessary to down sampling the author data in order to optimize user loading
    // experience.
    ArrayList dataArrayList = author.getData();
    if (dataArrayList.size() >= 2000) {
      return author;
    }
    return author;
  }

  public static Integer limitPagesize(Integer pagesize) {
    if ((pagesize <= BIG_SIZE.getValue()) && (pagesize > 0)) {
      return pagesize;
    } else {
      return 0;
    }
  }
}
