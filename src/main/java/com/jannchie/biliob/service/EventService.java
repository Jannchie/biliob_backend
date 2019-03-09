package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Event;
import com.jannchie.biliob.model.FansVariation;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface EventService {

  /**
   * Get the data of important events.
   *
   * @param page page number
   * @param pagesize page size
   * @return a slice of events
   */
  MySlice<Event> pageEvent(Integer page, Integer pagesize);
  /**
   * Get the data of important fans variation.
   *
   * @param page page number
   * @param pagesize page size
   * @return a slice of fans variation events
   */
  Page<FansVariation> listFansVariation(Integer page, Integer pagesize);
}
