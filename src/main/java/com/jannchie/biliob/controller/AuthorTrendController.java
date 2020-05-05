package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.AuthorTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class AuthorTrendController {
    @Autowired
    AuthorTrendService authorTrendService;
}
