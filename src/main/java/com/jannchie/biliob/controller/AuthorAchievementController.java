package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AuthorAchievementService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/achievement")
    public List<Author.Achievement> getAuthorAchievementByLevel(@RequestParam(value = "lv", defaultValue = "5") Integer level) {
        return authorAchievementService.getAuthorAchievementByLevel(level);
    }
}
