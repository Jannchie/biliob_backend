package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jannchie
 */
@Service
public class CreditService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Result<User> doCreditOperation(CreditConstant creditConstant, String message) {
        User user = UserUtils.getUser();
        Double credit = creditConstant.getValue();
        user.setCredit(user.getCredit() + credit);
        if (credit < 0) {
            user.setExp(user.getExp() - credit);
        }
        mongoTemplate.update(User.class).apply(Update.update("credit", user.getCredit()).set("exp", user.getExp()));
        mongoTemplate.insert(UserRecord.class).one(new UserRecord(user, creditConstant, message));
        return ResultEnum.SUCCEED.getResult(user);
    }
}
