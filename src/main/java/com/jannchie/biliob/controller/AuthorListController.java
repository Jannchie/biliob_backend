package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.AuthorListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Get the global information of bilibili.
 *
 * @author jannchie
 */
@RestController
public class AuthorListController {

    private final AuthorListService authorListService;

    @Autowired
    public AuthorListController(AuthorListService authorListService) {
        this.authorListService = authorListService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author/list")
    public ResponseEntity postAuthorList(
            @RequestBody @Valid String name) {
        return authorListService.postAuthorList(name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/list/fail")
    public ResponseEntity getFail() {
        return authorListService.getFail();
    }
}
