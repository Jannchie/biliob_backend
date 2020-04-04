package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jannchie
 */
@RestController
@RequestMapping("/api/admin")
public class ManagerController {
    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/data/queue")
    public HashMap<String, Long> getQueueCount() {
        Calendar c = Calendar.getInstance();

        HashMap<String, Long> result = new HashMap<>();
        Long countAuthor = mongoTemplate.count(Query.query(Criteria.where("next").gt(c.getTime())), AuthorIntervalRecord.class);
        Long countVideo = mongoTemplate.count(Query.query(Criteria.where("next").gt(c.getTime())), "video_interval");
        result.put("authorQueue", countAuthor);
        result.put("videoQueue", countVideo);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/data")
    public Result<?> setUserData(@RequestBody User user) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(user.getName())),
                Update.update("credit", user.getCredit()).set("exp", user.getExp()), User.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/fix-data")
    public Result<?> fixMinusUserData() {
        List<User> users = mongoTemplate.find(Query.query(Criteria.where("credit").lt(0)), User.class);
        for (User user : users
        ) {
            Double val = user.getCredit();

            mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(user.getName())),
                    Update.update("credit", user.getCredit() - val).set("exp", user.getExp() + val), User.class);
        }
        return new Result<>(ResultEnum.SUCCEED);
    }
}
