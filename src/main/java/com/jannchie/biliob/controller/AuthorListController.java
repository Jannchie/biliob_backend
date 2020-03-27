package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.AuthorList;
import com.jannchie.biliob.service.AuthorListService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public Result<AuthorList> initAuthorList(
            @RequestBody @Valid AuthorList authorList) {
        return authorListService.initAuthorList(authorList.getName(), authorList.getDesc(), authorList.getTagList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/list")
    public List<AuthorList> listAuthorList(
            @RequestParam(value = "p", defaultValue = "1") Long page,
            @RequestParam(value = "ps", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "kw", defaultValue = "") String keyword
    ) {
        return authorListService.listAuthorList(keyword, page, pageSize);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/author/list/{id}")
    public Result<?> listAuthorList(
            @PathVariable("id") String id
    ) {
        return authorListService.deleteAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/list/{id}")
    public AuthorList getAuthorList(
            @PathVariable("id") String id
    ) {
        return authorListService.getAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/author/list/{id}/star")
    public Result<?> starAuthorList(
            @PathVariable("id") String id
    ) {
        return authorListService.starAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/author/list/{id}/star")
    public Result<?> unstarAuthorList(
            @PathVariable("id") String id
    ) {
        return authorListService.unstarAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/list/star")
    public List<AuthorList> listStaredAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorListService.listUserAuthorList(page, pageSize, 0);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/list/maintain")
    public List<AuthorList> listMaintainAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorListService.listUserAuthorList(page, pageSize, 1);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/list/create")
    public List<AuthorList> listCreatedAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorListService.listUserAuthorList(page, pageSize, 2);
    }
    // TODO: Modify List's Author
}
