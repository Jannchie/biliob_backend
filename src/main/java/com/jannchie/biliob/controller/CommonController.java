package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Notice;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
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

/**
 * @author Jannchie
 */
@RestController
public class CommonController {

    @Autowired
    MongoTemplate mongoTemplate;
    private Logger logger = LogManager.getLogger();

    @RequestMapping(method = RequestMethod.GET, value = "/api/common/notice")
    public Notice getNotice() {
        logger.debug("[{}] 获取通知", UserUtils.getUsername());
        return mongoTemplate.findOne(new Query().with(Sort.by("date").descending()), Notice.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/admin/common/notice")
    public Result<?> modifyNotice(@RequestBody @Validated Notice notice) {
        notice.setDate(Calendar.getInstance().getTime());
        mongoTemplate.insert(notice);
        logger.info("[{}] 发布通知", UserUtils.getUsername());
        return new Result<>(ResultEnum.SUCCEED);
    }
}
