package com.jannchie.biliob.controller;

import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.VideoAlreadyFocusedException;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.service.serviceImpl.VideoServiceImpl;
import com.jannchie.biliob.utils.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 测试控制器
 *
 * @author jannchie
 */
@RestController
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/video/{aid}")
    public Video getVideoDetails(@PathVariable("aid") Long aid) {
        return videoService.getVideoDetails(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/author/{mid}/video/{aid}")
    public Slice<Video> getAuthorVideo(@PathVariable("aid") Long aid,
                                       @PathVariable("mid") Long mid,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "5") Integer pagesize) {
        return videoService.getAuthorVideo(aid, mid, page, pagesize);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/video")
    public ResponseEntity<Message> postVideoByAid(@RequestBody @Valid Video video) throws UserAlreadyFavoriteVideoException, VideoAlreadyFocusedException {
        videoService.postVideoByAid(video.getAid());
        return new ResponseEntity<Message>(new Message(200, "观测视频成功"), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/video")
    public Page<Video> getVideo(@RequestParam(defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "20") Integer pageSize,
                                @RequestParam(defaultValue = "-1") Long aid,
                                @RequestParam(defaultValue = "") String text) {
        return videoService.getVideo(aid, text, page, pageSize);
    }
}