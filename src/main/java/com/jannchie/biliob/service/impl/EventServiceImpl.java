package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.model.Event;
import com.jannchie.biliob.model.FansVariation;
import com.jannchie.biliob.repository.EventRepository;
import com.jannchie.biliob.repository.FansVariationRepository;
import com.jannchie.biliob.service.EventService;
import com.jannchie.biliob.utils.MySlice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
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
    private final FansVariationRepository fansVariationRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, FansVariationRepository fansVariationRepository) {
        this.eventRepository = eventRepository;
        this.fansVariationRepository = fansVariationRepository;
    }

    /**
     * Get the data of important events.
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of events
     */
    @Override
    public MySlice<Event> pageEvent(Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            return null;
        }
        Slice<Event> e =
                eventRepository.findAll(
                        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "datetime")));
        logger.info("获取事件");
        return new MySlice<>(e);
    }

    /**
     * Get the data of important fans variation.
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of fans variation events
     */
    @Override
    public Page<FansVariation> listFansVariation(Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            return null;
        }
        logger.info("获取事件");
        return fansVariationRepository.findAll(
                PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "datetime")));
    }
}
