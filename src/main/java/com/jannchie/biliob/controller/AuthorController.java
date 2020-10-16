package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.service.AdminService;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.utils.IpUtil;
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.MySlice;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private Logger logger = LogManager.getLogger();

    @Autowired
    public AuthorController(AuthorService authorService, AdminService adminService) {
        this.authorService = authorService;
        this.adminService = adminService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}/history-data")
    public List<Author.Data> getAuthorHistoryDetails(
            @PathVariable("mid") Long mid) {
        logger.info("获取mid: [{}]的历史数据", mid);
        return authorService.getHistoryData(mid);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}")
    public Author getAuthorDetails(
            @PathVariable("mid") Long mid, @RequestParam(defaultValue = "1") Integer type) {
        logger.info("获取mid: [{}]的详细数据", mid);
        return authorService.getAuthorDetails(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/author")
    public ResponseEntity<?> postAuthorByMid(@RequestBody @Valid Long mid)
            throws UserAlreadyFavoriteAuthorException, AuthorAlreadyFocusedException {
        logger.info("添加UP主到观测系统 mid: [{}]", mid);
        if (UserUtils.getUser() == null) {
            return new ResponseEntity<>(ResultEnum.HAS_NOT_LOGGED_IN.getResult(), HttpStatus.OK);
        }
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
        logger.info("获取分页的UP排名列表，sort: [{}], page: [{}], pageSize: [{}], mid: [{}] text: [{}]", sort, page, pageSize, mid, text);
        if (page > 30) {
            adminService.banIp(IpUtil.getIpAddress(request), "访问无效页数");
            page = 1;
        }
        return authorService.getAuthor(mid, text, page, pageSize, sort);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}/info")
    public Author getAuthorInfo(@PathVariable("mid") Long mid) {
        logger.info("获取UP主的信息，mid: [{}]", mid);
        return authorService.getAuthorInfo(mid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/fans-increase-rate")
    public ResponseEntity<?> listFansIncreaseRate() {
        logger.info("列出实时涨粉榜");
        return authorService.listFansIncreaseRate();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/fans-decrease-rate")
    public ResponseEntity<?> listFansDecreaseRate() {
        logger.info("列出实时掉粉榜");
        return authorService.listFansDecreaseRate();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/real-time")
    public ResponseEntity<?> listRealTime(
            @RequestParam(defaultValue = "0") Long aMid, @RequestParam(defaultValue = "0") Long bMid) {
        logger.info("查看实时数据");
        return authorService.getRealTimeData(aMid, bMid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/top")
    public ResponseEntity<?> getTopAuthor() {
        logger.info("获取TOP UP主");
        return authorService.getTopAuthor();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/top/refresh")
    public ResponseEntity<?> getLatestTopAuthorData() {
        logger.info("获取最新的TOP UP主数据");
        return authorService.getLatestTopAuthorData();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/tag")
    public List<?> listAuthorTag(
            @RequestParam(defaultValue = "0") Long mid,
            @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("获取UP主TAG数据，mid:[{}]", mid);
        return authorService.listAuthorTag(mid, limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/{mid}/relationship")
    public List<?> listRelateAuthor(
            @PathVariable("mid") Long mid, @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("根据mid获取相关作者，mid: [{}]", mid);
        return authorService.listRelatedAuthorByMid(mid, limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/hot")
    public List<?> listHotAuthor() {
        logger.info("列出热搜UP主");
        return authorService.listHotAuthor();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/author/compare")
    public List<Author> compareAuthor(
            @RequestParam(defaultValue = "", value = "mid") String mids,
            @RequestParam(defaultValue = "0", value = "type") Integer type
    ) {
        logger.info("获取多个UP主的数据，mid: [{}]", mids);
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
        logger.info("获取主页TOP粉丝数对比数据");
        return authorService.getHomePageCompareAuthors();
    }
}
