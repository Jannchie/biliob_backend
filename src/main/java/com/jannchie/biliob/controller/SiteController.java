package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Site;
import com.jannchie.biliob.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

  @RequestMapping(method = RequestMethod.GET, value = "/api/site/play-online")
  public List<Site> getPlayOnline(@RequestParam(defaultValue = "1") Integer days) {
    return siteService.getPlayOnline(days);
  }
}
