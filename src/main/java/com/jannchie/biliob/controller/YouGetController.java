package com.jannchie.biliob.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jannchie
 */
@RestController
public class YouGetController {

    private static final Logger logger = LogManager.getLogger();

    @RequestMapping(method = RequestMethod.GET, value = "/api/video/download/{aid}/{page}", produces = "application/json")
    public ResponseEntity pageEvent(@PathVariable("aid") Long aid, @PathVariable("page") Integer page) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(String.format("you-get https://www.bilibili.com/video/av%d?p=%d --json", aid, page));
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        StringBuilder o = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            o.append(line);
        }
        p.waitFor();
        is.close();
        reader.close();
        p.destroy();
        logger.info("[GET] 获取AV{}第{}P视频的下载链接", aid, page);
        return ResponseEntity.ok(o.toString());
    }
}
