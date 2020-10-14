package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.BangumiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get the Bangumi Data.
 *
 * @author jannchie
 */
@RestController
public class BangumiController {

    private final BangumiService bangumiService;

    @Autowired
    public BangumiController(BangumiService bangumiService) {
        this.bangumiService = bangumiService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/bangumi")
    public ResponseEntity<?> listOnline(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return bangumiService.listBangumi(page, pagesize);
    }
}
