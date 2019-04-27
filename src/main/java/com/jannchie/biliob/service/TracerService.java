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
}
