package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.form.AddCreditToUserForm;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    CreditOperateHandle creditOperateHandle;

    @RequestMapping(method = RequestMethod.GET, value = "/data/queue")
    public HashMap<String, Long> getQueueCount() {
        Calendar c = Calendar.getInstance();
        HashMap<String, Long> result = new HashMap<>();
        Long countAuthor = mongoTemplate.count(Query.query(Criteria.where("next").lt(c.getTime())), AuthorIntervalRecord.class);
        Long countVideo = mongoTemplate.count(Query.query(Criteria.where("next").lt(c.getTime())), "video_interval");
        result.put("authorQueue", countAuthor);
        result.put("videoQueue", countVideo);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/credit")
    public Result<?> giveCredit(@RequestBody AddCreditToUserForm form) {
        User u = mongoTemplate.findOne(Query.query(Criteria.where("name").is(form.getName())), User.class);
        return creditOperateHandle.doCustomCreditOperate(u, -form.getCredit(), CreditConstant.GIVE_CREDIT, form.getMsg(), () -> null);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/user/data")
    public Result<?> setUserData(@RequestBody User user) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(user.getName())),
                Update.update("credit", user.getCredit()).set("exp", user.getExp()), User.class);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/grand/{name}/{role}")
    public Result<?> grandRole(@PathVariable("name") String name, @PathVariable("role") String role) {
        User user = UserUtils.getFullInfo();
        Integer level = RoleEnum.getLevelByName(user.getRole());
        if (level < 8) {
            return new Result<>(ResultEnum.PERMISSION_DENIED);
        }
        mongoTemplate.updateFirst(Query.query(Criteria.where("name").is(name)), Update.update("role", role), User.class);
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
