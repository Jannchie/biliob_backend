package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Notice;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
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

    @RequestMapping(method = RequestMethod.GET, value = "/api/common/notice")
    public Notice getNotice() {
        return mongoTemplate.findOne(new Query().with(Sort.by("date").descending()), Notice.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/admin/common/notice")
    public Result<?> modifyNotice(String msg, Integer type) {
        Notice notice = new Notice();
        notice.setDate(Calendar.getInstance().getTime());
        notice.setMsg(msg);
        notice.setType(type);
        mongoTemplate.insert(notice);
        return new Result<>(ResultEnum.SUCCEED);
    }
}
