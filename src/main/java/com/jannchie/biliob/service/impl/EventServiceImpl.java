package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.model.Event;
import com.jannchie.biliob.repository.EventRepository;
import com.jannchie.biliob.service.EventService;
import com.jannchie.biliob.utils.MySlice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
@CacheConfig(cacheNames = "event")
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
   * @param page page number
   * @param pagesize page size
   * @return a slice of events
   */
  @Override
  @Cacheable(value = "event", key = "#page")
  public MySlice<Event> pageEvent(Integer page, Integer pagesize) {
    if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
      return null;
    }
    Slice<Event> e = eventRepository.findAll(
        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "datetime")));
    logger.info("获取事件");
    return new MySlice<>(e);
  }
}
