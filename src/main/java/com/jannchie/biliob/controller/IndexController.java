package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.JannchieIndex;
import com.jannchie.biliob.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jannchie
 */
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;

    @RequestMapping(method = RequestMethod.GET, value = "/api/index")
    public JannchieIndex getIndex(@RequestParam(name = "keyword") String keyword) {
        return indexService.getIndex(keyword);
    }

}
