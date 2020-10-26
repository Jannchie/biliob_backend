package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.IndexServiceV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Jannchie
 */
@RestController
public class IndexControllerV2 {
    Logger logger = LogManager.getLogger();
    @Autowired
    IndexServiceV2 indexService;

    @RequestMapping(method = RequestMethod.GET, value = "/api/index/v2/")
    public Document getIndex(@RequestParam(name = "keyword") String keyword) {
        return indexService.getIndex(keyword);
    }
}
