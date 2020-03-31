package com.jannchie.biliob.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
@RequestMapping("/api/admin")
public class ManagerController {
    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/u")
    public ResponseEntity getPendingQuestion(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return null;
    }
}
