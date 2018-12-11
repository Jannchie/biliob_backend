package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.repository.BangumiRepository;
import com.jannchie.biliob.repository.DonghuaRepository;
import com.jannchie.biliob.service.BangumiService;
import com.jannchie.biliob.service.DonghuaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public class DonghuaServiceImpl implements DonghuaService {

  private static final Integer MAX_PAGE_SIZE = 20;

  private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
  private final DonghuaRepository donghuaRepository;

  public DonghuaServiceImpl(DonghuaRepository donghuaRepository) {
    this.donghuaRepository = donghuaRepository;
  }

  /**
   * Get the data of bangumi list, including bangumi name, pts and tags.
   *
   * @return Online number.
   */
  @Override
  public ResponseEntity listDonghua(Integer page, Integer pagesize) {
    if (pagesize > MAX_PAGE_SIZE) {
      pagesize = MAX_PAGE_SIZE;
    }
    logger.info("获得国创列表");
    return new ResponseEntity<>(
        donghuaRepository.findAll(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC,"currentPts"))), HttpStatus.OK);
  }
}
