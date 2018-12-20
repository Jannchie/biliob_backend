package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
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
  ResponseEntity pageEvent(Integer page, Integer pagesize);
}
