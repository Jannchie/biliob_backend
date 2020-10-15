package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Notice;
import com.jannchie.biliob.utils.BiliobUtils;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
@RestController
public class CommonController {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BiliobUtils biliobUtils;
    private Logger logger = LogManager.getLogger();

    @RequestMapping(method = RequestMethod.GET, value = "/api/common/notice")
    public Notice getNotice() {
        logger.debug("[{}] 获取通知", biliobUtils.getUserName());
        return mongoTemplate.findOne(new Query().with(Sort.by("date").descending()), Notice.class);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/common/notices")
    public List<Notice> listNotices() {
        logger.debug("[{}] 获取最近通知", biliobUtils.getUserName());
        return mongoTemplate.find(new Query().with(Sort.by("date").descending()).limit(4), Notice.class);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/api/admin/common/notice")
    public Result<?> modifyNotice(@RequestBody @Validated Notice notice) {
        notice.setDate(Calendar.getInstance().getTime());
        mongoTemplate.insert(notice);
        logger.info("[{}] 发布通知", biliobUtils.getUserName());
        return new Result<>(ResultEnum.SUCCEED);
    }
}
