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
public class BankController {
    VideoServiceV2 videoService = new VideoServiceV2();

    @Autowired
    public BankController(VideoServiceV2 videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/bank/info")
    public Result<?> getBankInfo() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/bank/save")
    public Result<?> saveCredit() {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/bank/withdrawal")
    public Result<?> withdrawalCredit() {
        return null;
    }
}
