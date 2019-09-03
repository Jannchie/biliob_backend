package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.TracerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/history-queue")
    public ResponseEntity getHistoryQueueStatus() {
        return tracerService.getHistoryQueueStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/video-queue")
    public ResponseEntity getVideoQueueStatus() {
        return tracerService.getVideoQueueStatus();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/exists-tasks")
    public ResponseEntity sliceExistsTask(
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "0") Integer page) {
        return tracerService.sliceExistsTask(page, pagesize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/spider-tasks")
    public ResponseEntity sliceSpiderTask(
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "0") Integer type) {
        return tracerService.sliceSpiderTask(page, pagesize, type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/progress-task")
    public ResponseEntity sliceProgressTask(
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "0") Integer page) {
        return tracerService.sliceProgressTask(page, pagesize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/dashboard")
    public ResponseEntity getDashboardData() {
        return tracerService.getDashboardData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/latest-progress")
    public ResponseEntity getLatestProgress() {
        return tracerService.getLatestProgressTaskResponse();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/latest-spider")
    public ResponseEntity getLatestSpider() {
        return tracerService.getLatestSpiderTaskResponse();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/visit/author")
    public ResponseEntity listAuthorVisitRecord(@RequestParam(defaultValue = "7") Integer limit) {
        return tracerService.listAuthorVisitRecord(limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/tracer/visit/video")
    public ResponseEntity listVideoVisitRecord(@RequestParam(defaultValue = "7") Integer limit) {
        return tracerService.listVideoVisitRecord(limit);
    }
}
