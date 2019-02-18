package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Event;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
public interface EventService {

  /**
   * Get the data of important events.
   *
   * @param page     page number
   * @param pagesize page size
   * @return a slice of events
   */
  MySlice<Event> pageEvent(Integer page, Integer pagesize);
}
