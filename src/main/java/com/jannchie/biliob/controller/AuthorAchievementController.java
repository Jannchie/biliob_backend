package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AuthorAchievementService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class AuthorAchievementController {
    @Autowired
    AuthorAchievementService authorAchievementService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/author/achievement")
    public Result<?> postAnalyzeRequest(@RequestBody Author author) {
        return authorAchievementService.analyzeAuthorAchievement(author.getMid());
    }

}
