package com.jannchie.biliob.controller;

import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Jannchie
 */
@Controller
public class DamnYouController {
    MongoTemplate mongoTemplate;

    @Autowired
    public DamnYouController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/damnYou")
    public ResponseEntity<Result<String>> postData(
            @RequestBody @Valid List<Object> data) {
        return null;
    }

}
