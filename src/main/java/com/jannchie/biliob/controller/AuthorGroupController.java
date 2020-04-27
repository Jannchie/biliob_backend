package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.AuthorGroup;
import com.jannchie.biliob.model.GroupUpdateRecord;
import com.jannchie.biliob.service.AuthorGroupService;
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
public class AuthorGroupController {

    private final AuthorGroupService authorGroupService;

    @Autowired
    public AuthorGroupController(AuthorGroupService authorGroupService) {
        this.authorGroupService = authorGroupService;

    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author/group")
    public Result<AuthorGroup> initAuthorList(
            @RequestBody @Valid AuthorGroup authorGroup) {
        return authorGroupService.initAuthorList(authorGroup.getName(), authorGroup.getDesc(), authorGroup.getTagList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/author/group/{gid}/edit")
    public Result<?> editAuthorList(
            @PathVariable("gid") String id,
            @RequestBody @Valid AuthorGroup authorGroup) {
        return authorGroupService.editAuthorList(id, authorGroup.getName(), authorGroup.getDesc(), authorGroup.getTagList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/group")
    public List<AuthorGroup> listAuthorList(
            @RequestParam(value = "p", defaultValue = "1") Long page,
            @RequestParam(value = "ps", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "kw", defaultValue = "") String keyword
    ) {
        return authorGroupService.listAuthorList(keyword, page, pageSize);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/author/group/{id}")
    public Result<?> listAuthorList(
            @PathVariable("id") String id
    ) {
        return authorGroupService.deleteAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/group/{id}")
    public AuthorGroup getAuthorList(
            @PathVariable("id") String id
    ) {
        return authorGroupService.getAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/author/group/{id}/star")
    public Result<?> starAuthorList(
            @PathVariable("id") String id
    ) {
        return authorGroupService.starAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/author/group/{id}/star")
    public Result<?> unstarAuthorList(
            @PathVariable("id") String id
    ) {
        return authorGroupService.unstarAuthorList(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/group/star")
    public List<AuthorGroup> listStaredAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorGroupService.listUserAuthorList(page, pageSize, 0);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/group/maintain")
    public List<AuthorGroup> listMaintainAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorGroupService.listUserAuthorList(page, pageSize, 1);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author/group/create")
    public List<AuthorGroup> listCreatedAuthorList(
            @RequestParam(value = "p") Integer page,
            @RequestParam(value = "ps") Integer pageSize
    ) {
        return authorGroupService.listUserAuthorList(page, pageSize, 2);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/author/group/{gid}/add/{mid}")
    public Result<?> addAuthorToGroup(@PathVariable("gid") String gid, @PathVariable("mid") Long mid) {
        return authorGroupService.addAuthorToGroup(gid, mid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/author/group/{gid}/del/{mid}")
    public Result<?> deleteAuthorFromGroup(@PathVariable("gid") String gid, @PathVariable("mid") Long mid) {
        return authorGroupService.deleteAuthorFromGroup(gid, mid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/group/{gid}/log")
    public List<GroupUpdateRecord> listChangeLog(@PathVariable("gid") String gid) {
        return authorGroupService.listChangeLog(gid);
    }
}
