package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 测试控制器
 *
 * @author jannchie
 */
@RestController
public class AuthorController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/author/{mid}")
    public Author getAuthorDetails(@PathVariable("mid") Long mid) {
        logger.info("[GET]AuthorMid = " + mid);
        return authorService.getAuthorDetails(mid);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/author")
    public Author postAuthorByMid(@RequestParam("mid") Long mid) {
        logger.info("[POST]AuthorMid = " + mid);
        return authorService.postAuthorByMid(mid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/author")
    public Page<Author> getAuthor(@RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "20") Integer pageSize,
                                  @RequestParam(defaultValue = "-1") Long mid,
                                  @RequestParam(defaultValue = "") String text) {
        logger.info("[GET]SearchAuthor:mid:" + mid + ",text:" + text);
        return authorService.getAuthor(mid, text, page, pageSize);
    }
}