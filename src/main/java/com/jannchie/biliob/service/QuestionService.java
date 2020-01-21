package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
public interface QuestionService {

    /**
     * get a slice of handled question
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of questions
     */
    ResponseEntity getHandledQuestion(Integer page, Integer pagesize);

    /**
     * get a slice of pending question
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of questions
     */
    ResponseEntity getPendingQuestion(Integer page, Integer pagesize);
}
