package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 测试控制器
 *
 * @author jannchie
 */
@RestController
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/video/{aid}")
    public Video getVideoDetails(@PathVariable("aid") Long aid) {
        logger.info("[GET]VideoAid = " + aid);
        return videoService.getVideoDetails(aid);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/video")
    public Video postAuthorByMid(@RequestParam("aid") Long aid) {
        logger.info("[POST]VideoAid = " + aid);
        return videoService.postVideoByAid(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/video")
    public Page<Video> getVideo(@RequestParam(defaultValue = "5") Integer page, @RequestParam(defaultValue = "20") Integer pageSize) {
        return videoService.getAuthor(page, pageSize);
    }
}