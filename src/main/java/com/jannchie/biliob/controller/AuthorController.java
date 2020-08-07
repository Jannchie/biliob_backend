package com.jannchie.biliob.controller;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AdminService;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.utils.IpUtil;
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jannchie
 */
@RestController
public class AuthorController {

    private AuthorService authorService;
    private AdminService adminService;


    @Autowired
    public AuthorController(AuthorService authorService, AdminService adminService) {
        this.authorService = authorService;
        this.adminService = adminService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}")
    public Author getAuthorDetails(
            @PathVariable("mid") Long mid, @RequestParam(defaultValue = "1") Integer type) {
        return authorService.getAuthorDetails(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author")
    public ResponseEntity<Message> postAuthorByMid(@RequestBody @Valid Long mid)
            throws UserAlreadyFavoriteAuthorException, AuthorAlreadyFocusedException {
        authorService.postAuthorByMid(mid);
        return new ResponseEntity<>(new Message(200, "观测UP主成功"), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author")
    public MySlice<Author> getAuthor(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "-1") Long mid,
            @RequestParam(defaultValue = "") String text) {
        if (page > 30) {
            adminService.banIp(IpUtil.getIpAddress(request), "访问无效页数");
            page = 1;
        }
        return authorService.getAuthor(mid, text, page, pageSize, sort);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}/info")
    public Author getAuthorInfo(@PathVariable("mid") Long mid) {
        return authorService.getAuthorInfo(mid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/fans-increase-rate")
    public ResponseEntity listFansIncreaseRate() {
        return authorService.listFansIncreaseRate();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/fans-decrease-rate")
    public ResponseEntity listFansDecreaseRate() {
        return authorService.listFansDecreaseRate();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/real-time")
    public ResponseEntity listRealTime(
            @RequestParam(defaultValue = "0") Long aMid, @RequestParam(defaultValue = "0") Long bMid) {
        return authorService.getRealTimeData(aMid, bMid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/top")
    public ResponseEntity getTopAuthor() {
        return authorService.getTopAuthor();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/top/refresh")
    public ResponseEntity getLatestTopAuthorData() {
        return authorService.getLatestTopAuthorData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/tag")
    public List listAuthorTag(
            @RequestParam(defaultValue = "0") Long mid,
            @RequestParam(defaultValue = "10") Integer limit) {
        return authorService.listAuthorTag(mid, limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}/relationship")
    public List listRelateAuthor(
            @PathVariable("mid") Long mid, @RequestParam(defaultValue = "10") Integer limit) {
        return authorService.listRelatedAuthorByMid(mid, limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/hot")
    public List listHotAuthor() {
        return authorService.listHotAuthor();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/compare")
    public List<Author> compareAuthor(
            @RequestParam(defaultValue = "", value = "mid") String mids,
            @RequestParam(defaultValue = "0", value = "type") Integer type
    ) {
        Stream<Author> authors = Arrays.stream(mids.split(",")).map((midString) -> {
            Long mid = Long.valueOf(midString);
            if (type == 0) {
                return authorService.getAuthorInfo(mid);
            } else {
                return authorService.getAuthorDetails(mid);
            }
        });
        return authors.collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/compare/top-fans")
    public List<Author> compareAuthor() {
        return authorService.getHomePageCompareAuthors();
    }
}
