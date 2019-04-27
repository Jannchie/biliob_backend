package com.jannchie.biliob.controller;

import com.jannchie.biliob.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Get the global information of bilibili.
 *
 * @author jannchie
 */
@RestController
public class SiteController {

  private final SiteService siteService;

  @Autowired
  public SiteController(SiteService siteService) {
    this.siteService = siteService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/site")
  public ResponseEntity listOnline(@RequestParam(defaultValue = "1") Integer days) {
    return siteService.listOnline(days);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/site/counter")
  public Map getBiliOBCounter() {
    return siteService.getBiliOBCounter();
  }
}
