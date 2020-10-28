package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.VideoInfo;
import com.jannchie.biliob.model.VideoStat;
import com.jannchie.biliob.service.VideoServiceV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jannchie
 */
@RestController
public class VideoControllerV3 {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    VideoServiceV3 videoService;

    @Autowired
    public VideoControllerV3(VideoServiceV3 videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/av{aid}/info")
    public VideoInfo getVideoInfo(
            @PathVariable("aid") Long aid) {
        logger.info("获得视频信息[aid: {}]", aid);
        return videoService.getVideoInfo(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/BV{bvid}/info")
    public VideoInfo getVideoInfo(
            @PathVariable("bvid") String bvid) {
        logger.info("获得视频信息[bvid: {}]", bvid);
        return videoService.getVideoInfo(bvid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/av{aid}/stat")
    public List<VideoStat> listVideoStats(
            @PathVariable("aid") Long aid) {
        logger.info("获得视频历史[aid: {}]", aid);
        return videoService.listVideoStat(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/BV{bvid}/stat")
    public List<VideoStat> listVideoStats(
            @PathVariable("bvid") String bvid) {
        logger.info("获得视频历史[bvid: {}]", bvid);
        return videoService.listVideoStat(bvid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/average")
    public Document getAverageByTagData(
            @RequestParam(defaultValue = "-1") Integer tid,
            @RequestParam(defaultValue = "-1") Long pubdate,
            @RequestParam(defaultValue = "-1") Long mid) {
        logger.info("获得平均[tid: {}, mid: {}, pubdate: {}]", tid, mid, pubdate);
        return videoService.getAverage(tid, mid, pubdate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/ad")
    public List<VideoInfo> listAd() {
        logger.info("获得广告");
        return videoService.listAd();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/list")
    public List<VideoInfo> listSearch(@RequestParam(defaultValue = "") String w,
                                      @RequestParam(defaultValue = "1") Integer p,
                                      @RequestParam(defaultValue = "20") Integer ps,
                                      @RequestParam(defaultValue = "0") Long d,
                                      @RequestParam(defaultValue = "view") String s) {
        logger.info("列出视频[w: {}, p: {}, ps: {}, s: {}, d: {}]", w, p, ps, s, d);
        return videoService.listSearch(w, p, ps, s, d);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/author")
    public List<VideoInfo> listAuthorVideo(@RequestParam(defaultValue = "-1") Long mid,
                                           @RequestParam(defaultValue = "view") String sort) {
        logger.info("列出作者视频[mid: {}, sort: {}]", mid, sort);
        return videoService.listAuthorVideo(mid, sort);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/topic-author")
    public List<Document> listTopicAuthor(@RequestParam(defaultValue = "") String kw, @RequestParam(defaultValue = "8") Integer limit) {
        logger.info("列出话题[{}]的相关作者，共[{}]个", kw, limit);
        return videoService.listTopicAuthor(kw, limit);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/index")
    public List<Document> listKeywordIndex(@RequestParam(defaultValue = "") String kw) {
        logger.info("列出关键词[{}]的相关指数", kw);
        return videoService.listKeywordIndex(kw);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/v3/popular-tag")
    public List<Document> listKeywordIndex(@RequestParam(defaultValue = "3") Integer d) {
        logger.info("列出近[{}]天热门关键词", d);
        return videoService.listTopTag(d);
    }
}
