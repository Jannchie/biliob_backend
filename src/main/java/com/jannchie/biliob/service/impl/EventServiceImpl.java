package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.model.Event;
import com.jannchie.biliob.repository.EventRepository;
import com.jannchie.biliob.service.EventService;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.jannchie.biliob.constant.ResultEnum.PARAM_ERROR;

/**
 * @author jannchie
 */
@Service
public class EventServiceImpl implements EventService {

  private static final Logger logger = LogManager.getLogger();
  private final EventRepository eventRepository;

  @Autowired
  public EventServiceImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Get the data of important events.
   *
   * @param page     page number
   * @param pagesize page size
   * @return a slice of events
   */
  @Override
  public ResponseEntity pageEvent(Integer page, Integer pagesize) {
    if(pagesize > PageSizeEnum.BIG_SIZE.getValue()){
      return new ResponseEntity<>(new Result(PARAM_ERROR),HttpStatus.FORBIDDEN);
    }
    Slice<Event> events = eventRepository.findAll(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "datetime")));
    logger.info("获取事件");
    return new ResponseEntity<>(events, HttpStatus.OK);
  }
}
