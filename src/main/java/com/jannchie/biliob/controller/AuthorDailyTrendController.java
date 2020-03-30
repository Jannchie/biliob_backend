package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.AuthorDailyTrend;
import com.jannchie.biliob.service.AuthorDailyTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author jannchie
 */
@RestController
public class AuthorDailyTrendController {
    @Autowired
    private AuthorDailyTrendService authorDailyTrendService;

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/trend")
    public List<AuthorDailyTrend> listAuthorDailyTopTrend(
            @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(value = "key") String key
    ) {
        return authorDailyTrendService.listAuthorDailyTopTrend(date, key);
    }
}
