package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/** @author jannchie */
@Service
public interface TracerService {
  /**
   * It is the function to get authors' queue status.
   *
   * @return The authors' queue status.
   */
  ResponseEntity getAuthorQueueStatus();

  /**
   * It is the function to get videos' queue status.
   *
   * @return The videos' queue status.
   */
  ResponseEntity getVideoQueueStatus();

  /**
   * Get the slice of exists task of the system.
   *
   * <p>
   *
   * <p>It is able to get the status of Biliob scheduler and Biliob spider.
   *
   * @param page The page number of the task slice.
   * @param pagesize The page size of the task slice.
   * @return tTe slice of exists task of the system.
   */
  ResponseEntity sliceExistsTask(Integer page, Integer pagesize);

  /**
   * Get the slice of progress task of the system.
   *
   * <p>
   *
   * <p>It is able to get the status of Biliob link generate task.
   *
   * @param page The page number of the task slice.
   * @param pagesize The page size of the task slice.
   * @return tTe slice of exists task of the system.
   */
  ResponseEntity sliceProgressTask(Integer page, Integer pagesize);
}
