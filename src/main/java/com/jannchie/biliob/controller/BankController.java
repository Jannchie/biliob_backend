package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.service.VideoServiceV2;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class VideoControllerV2 {
    VideoServiceV2 videoService = new VideoServiceV2();

    @Autowired
    public VideoControllerV2(VideoServiceV2 videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/video/v2/av{aid}")
    public Result<?> addVideoObserveTask(
            @PathVariable("aid") Long aid) {
        return videoService.addVideoObserveTask(aid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/video/v2/BV{bvid}")
    public Result<?> addVideoObserveTask(
            @PathVariable("bvid") String bvid) {
        return videoService.addVideoObserveTask(bvid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v2/av{aid}")
    public Video getVideoDetailByAid(
            @PathVariable("aid") Long aid) {
        return videoService.getVideoDetailByAid(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v2/BV{bvid}")
    public Video getVideoDetails(
            @PathVariable("bvid") String bvid) {
        return videoService.getVideoDetailByBvid(bvid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/video/v2/BV{bvid}")
    public Result<?> refreshVideoInterval(
            @PathVariable("bvid") String bvid) {
        return videoService.refreshVideoInterval(bvid);
    }
}
