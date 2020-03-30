package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.GuessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class GuessingController {
    @Autowired
    GuessingService guessingService;
}
