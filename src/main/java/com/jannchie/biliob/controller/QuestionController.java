package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jannchie
 */
@RestController
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/question/handled")
    public ResponseEntity getHandledQuestion(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return questionService.getHandledQuestion(page, pagesize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/question/pending")
    public ResponseEntity getPendingQuestion(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return questionService.getPendingQuestion(page, pagesize);
    }
}
