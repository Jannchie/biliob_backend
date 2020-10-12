package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.VideoInfo;
import com.jannchie.biliob.model.VideoStat;
import com.jannchie.biliob.service.VideoServiceV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jannchie
 */
@RestController
public class VideoControllerV3 {
    VideoServiceV3 videoService;

    @Autowired
    public VideoControllerV3(VideoServiceV3 videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/av{aid}/info")
    public VideoInfo getVideoInfo(
            @PathVariable("aid") Long aid) {
        return videoService.getVideoInfo(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/BV{bvid}/info")
    public VideoInfo getVideoInfo(
            @PathVariable("bvid") String bvid) {
        return videoService.getVideoInfo(bvid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/av{aid}/stat")
    public List<VideoStat> listVideoStats(
            @PathVariable("aid") Long aid) {
        return videoService.listVideoStat(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/BV{bvid}/stat")
    public List<VideoStat> listVideoStats(
            @PathVariable("bvid") String bvid) {
        return videoService.listVideoStat(bvid);
    }
}
