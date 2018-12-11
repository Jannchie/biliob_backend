package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface DonghuaService {

  /**
   * Get the data of donghua list, including donghua name, pts and tags.
   *
   * @param page page number
   * @param pagesize page size
   * @return Online number.
   */
  ResponseEntity listDonghua(Integer page, Integer pagesize);
}
