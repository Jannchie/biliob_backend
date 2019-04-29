package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.TracerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get tracer data.
 *
 * @author jannchie
 */
@RestController
public class TracerController {

  private final TracerService tracerService;

  @Autowired
  public TracerController(TracerService tracerService) {
    this.tracerService = tracerService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/author-queue")
  public ResponseEntity getAuthorQueueStatus() {
    return tracerService.getAuthorQueueStatus();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/video-queue")
  public ResponseEntity getVideoQueueStatus() {
    return tracerService.getVideoQueueStatus();
  }
}
