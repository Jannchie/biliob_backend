package com.jannchie.biliob.utils.credit.calculator;

import com.jannchie.biliob.constant.ExceptionEnum;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.exception.BusinessException;
import com.jannchie.biliob.model.CheckIn;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Component
public class CheckInCreditCalculator extends AbstractCreditCalculator {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CheckInCreditCalculator(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResponseEntity execute(User user, ObjectId objectId) {
        Boolean isCheckedIn =
                mongoTemplate.exists(new Query(where("name").is(user.getName())), "check_in");
        String userName = user.getName();
        Double credit = user.getCredit();
        if (isCheckedIn) {
            throw new BusinessException(ExceptionEnum.ALREADY_SIGNED);
        } else {
            // 插入已签到集合
            mongoTemplate.insert(new CheckIn(userName), "check_in");

            // update execute status
            Query query = new Query(where("_id").is(objectId));
            Update update = new Update();
            update.set("isExecuted", true);
            mongoTemplate.updateFirst(query, update, "user_record");
        }
        return null;
    }

    @Override
    ResponseEntity getResponseEntity(HashMap data) {
        return new ResponseEntity<>(new Result(ResultEnum.SIGN_SUCCEED, data), HttpStatus.OK);
    }
}
