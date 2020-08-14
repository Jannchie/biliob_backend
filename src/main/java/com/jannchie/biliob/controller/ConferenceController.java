package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.VideoServiceV2;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jannchie
 */
@RestController
public class ConferenceController {
    VideoServiceV2 videoService;

    @Autowired
    public ConferenceController(VideoServiceV2 videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/conference/agenda")
    public Result<?> listAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda")
    public Result<?> postAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda/support")
    public Result<?> supportAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda/against")
    public Result<?> againstAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda/abstain")
    public Result<?> abstainAgenda() {
        return null;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda/done")
    public Result<?> finishAgenda() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/conference/agenda/doing")
    public Result<?> startAgenda() {
        return null;
    }

}
