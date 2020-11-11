package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.repository.BangumiRepository;
import com.jannchie.biliob.service.BangumiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
public class BangumiServiceImpl implements BangumiService {

    private static final Integer MAX_PAGE_SIZE = 20;

    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
    @Autowired
    private BangumiRepository bangumiRepository;


    /**
     * Get the data of bangumi list, including bangumi name, pts and tags.
     *
     * @return Online number.
     */
    @Override
    public ResponseEntity<?> listBangumi(Integer page, Integer pagesize) {
        if (pagesize > MAX_PAGE_SIZE) {
            pagesize = MAX_PAGE_SIZE;
        }
        logger.info("获得番剧列表");
        return null;
    }
}
