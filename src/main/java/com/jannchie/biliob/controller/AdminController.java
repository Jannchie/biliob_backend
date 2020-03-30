package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.ScheduleItem;
import com.jannchie.biliob.model.SearchMethod;
import com.jannchie.biliob.object.AuthorIntervalCount;
import com.jannchie.biliob.service.AdminService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Get the global information of bilibili.
 *
 * @author jannchie
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public List listUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "0") Integer sort,
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "30") Integer day) {
        return adminService.listUser(page, pagesize, sort, text, day);
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/admin/user/grant")
    public ResponseEntity grantUserAdminRole(@RequestParam @Valid String userName) {
        return adminService.grantUserAdminRole(userName);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/admin/user/cancel")
    public ResponseEntity cancelUserAdminRole(@RequestParam @Valid String userName) {
        return adminService.cancelUserAdminRole(userName);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/author-list")
    public ResponseEntity postAuthorCrawlList(@RequestBody Map authorListData) {
        return adminService.postAuthorCrawlList(authorListData);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{type}/search-method")
    public ResponseEntity saveSearchMethod(
            @RequestBody SearchMethod searchMethod, @PathVariable("type") String type) {
        searchMethod.setType(type);
        return adminService.saveSearchMethod(searchMethod);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{type}/search-method")
    public List listSearchMethod(@PathVariable("type") String type) {
        return adminService.listSearchMethod(type);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{type}/search-method")
    public ResponseEntity deleteSearchMethod(
            @PathVariable("type") String type, @RequestParam String name, @RequestParam String owner) {
        return adminService.delSearchMethod(type, name, owner);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/schedule/{type}")
    public ResponseEntity deleteCustomSchedule(
            @PathVariable("type") String type, @RequestParam String name, @RequestParam String owner) {
        return adminService.deleteCustomSchedule(type, name, owner);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/aggregation")
    public List aggregationUser(@RequestBody Map<String, Object> data) {
        return adminService.aggregateUser(
                (int) data.get("page"),
                (int) data.get("pagesize"),
                (int) data.get("day"),
                (String) data.get("matchField"),
                (String) data.get("matchMethod"),
                (String) data.get("matchValue"),
                (int) data.get("sort"),
                (String) data.get("orderBy"),
                (int) data.get("bucketType"),
                (String) data.get("bucket"),
                (String) data.get("groupByField"),
                (String) data.get("groupReference"),
                (String) data.get("groupKeyword"));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload/schedule")
    public ResponseEntity postUploadSchedule(@RequestBody ScheduleItem item) {
        return adminService.postUploadSchedule(item);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ip")
    public List listIpRecord(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "") String groupBy,
            @RequestParam(defaultValue = "") String regex,
            @RequestParam(defaultValue = "") String ip,
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "30") Integer day) {
        return adminService.listIpRecord(page, pagesize, groupBy, text, day, regex, ip);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ip")
    public Result banIp(
            @RequestBody String ip) {
        return adminService.banIp(ip);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ua")
    public ResponseEntity<Result<?>> postBanedUserAgent(@RequestBody String userAgent) {
        return adminService.banUserAgent(userAgent);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ip/dist")
    public Map<Integer, Integer> getIpIntDist(@RequestParam(defaultValue = "") String ip) {
        return adminService.getDistribute(ip);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/spider/author/interval-data")
    public List<AuthorIntervalCount> getSpiderStat() {
        return adminService.getSpiderStat();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ip/variance")
    public Double getIpIntVariance(@RequestParam(defaultValue = "") String ip) {
        return adminService.getVariance(ip);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/reduction")
    public Result<?> authorReduction() {
        return adminService.dataReduction();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/reduction/author/{mid}")
    public Result<?> deleteByMid(@PathVariable Long mid) {
        return adminService.reduceByMid(mid);
    }

}
