package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.VideoInfo;
import com.jannchie.biliob.model.VideoStat;
import com.jannchie.biliob.service.VideoServiceV3;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/average")
    public Document getAverageByTagData(
            @RequestParam(defaultValue = "-1") Integer tid,
            @RequestParam(defaultValue = "-1") Long pubdate,
            @RequestParam(defaultValue = "-1") Long mid) {
        return videoService.getAverage(tid, mid, pubdate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/ad")
    public List<VideoInfo> listAd() {
        return videoService.listAd();
    }
}
