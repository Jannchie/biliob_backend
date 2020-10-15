package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.MonitorService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jannchie
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Autowired
    MonitorService monitorService;

    @RequestMapping(method = RequestMethod.GET, value = "/spider/author")
    public List<Document> getCrawlRateAuthor() {
        return monitorService.getCrawlRateAuthor();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/spider/video")
    public List<Document> getCrawlRateVideo() {
        return monitorService.getCrawlRateVideo();
    }

}
