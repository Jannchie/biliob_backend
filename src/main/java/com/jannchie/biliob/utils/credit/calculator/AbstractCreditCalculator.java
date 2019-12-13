package com.jannchie.biliob.utils.credit.calculator;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ExceptionEnum;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.exception.BusinessException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.LoginChecker;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Component
public abstract class AbstractCreditCalculator<T> {

    private static final Logger logger = LogManager.getLogger(AbstractCreditCalculator.class);
    private final MongoTemplate mongoTemplate;
    private T data;
    private CreditConstant creditConstant;

    public AbstractCreditCalculator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AbstractCreditCalculator(MongoTemplate mongoTemplate, CreditConstant creditConstant) {
        this.mongoTemplate = mongoTemplate;
        this.creditConstant = creditConstant;
    }

    public static ResponseEntity getSuccessResponse(Double credit, Double exp) {
        ResponseEntity<Result> r;
        HashMap<String, Double> data;

        data = new HashMap<>(2);
        data.put("exp", exp);
        data.put("credit", credit);
        r = new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);

        return r;
    }

    private ResponseEntity<Result> checkCredit(User user, Double value) {
        if (user == null) {
            return new ResponseEntity<>(
                    new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        } else if (value < 0 && user.getCredit() < (-value)) {
            AbstractCreditCalculator.logger.info("用户：{},积分不足,当前积分：{}", user.getName(), user.getCredit());
            return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
        } else {
            return null;
        }
    }

    /**
     * Execute with an id; return user's new credit.
     *
     * @param creditConstant the operation value.
     * @param id             the id of author or video.
     * @return The response of user's request.
     */

    public ResponseEntity executeAndGetResponse(CreditConstant creditConstant, Long id) {
        this.creditConstant = creditConstant;
        User user = LoginChecker.checkInfo();
        Double value = creditConstant.getValue();
        ResponseEntity<Result> r = checkCredit(user, value);

        if (r != null) {
            return r;
        }
        Double credit = user.getCredit() + value;
        Double exp = user.getExp() + Math.abs(value);
        String userName = user.getName();

        // update record
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        UserRecord userRecord = new UserRecord(date, this.creditConstant.getMsg(id), value, userName);
        mongoTemplate.insert(userRecord, "user_record");
        ObjectId objectId = userRecord.getId();

        // execute
        ResponseEntity executeError = execute(id, objectId);
        if (executeError != null) {
            return executeError;
        }

        // update user info
        updateUserInfo(credit, exp, userName);

        // log
        log(value, user.getName(), this.creditConstant.getMsg(id));

        return getCreditResponse(credit, exp);
    }

    private ResponseEntity getCreditResponse(Double credit, Double exp) {
        return getSuccessResponse(credit, exp);
    }

    private void updateUserInfo(Double credit, Double exp, String userName) {
        Query query = new Query(where("name").is(userName));
        Update update = new Update();
        update.set("credit", credit);
        update.set("exp", exp);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    /**
     * Execute without id; return user's new credit.
     *
     * @param creditConstant the operation value.
     * @return The response of user's request.
     */

    public ResponseEntity executeAndGetResponse(CreditConstant creditConstant) {
        this.creditConstant = creditConstant;

        User user = LoginChecker.checkInfo();
        Double value = creditConstant.getValue();

        ResponseEntity<Result> r = checkCredit(user, value);

        if (r != null) {
            return r;
        }

        Double credit = user.getCredit() + value;
        Double exp = user.getExp() + Math.abs(value);

        String userName = user.getName();

        // update record
        ObjectId objectId = getObjectIdAndSaveRecord(creditConstant, value, userName);

        // execute
        ResponseEntity executeError = execute(user, objectId);
        if (executeError != null) {
            return executeError;
        }

        // update user info
        updateUserInfo(credit, exp, userName);

        // log
        log(value, user.getName(), creditConstant.getMsg());
        return getCreditResponse(credit, exp);
    }

    /**
     * Execute without id; return user's new credit.
     *
     * @param data the operation value.
     * @return The response of user's request.
     */

    public ResponseEntity executeAndGetResponse(T data) {

        User user = LoginChecker.checkInfo();
        Double value = this.creditConstant.getValue();

        ResponseEntity<Result> r = checkCredit(user, value);

        if (r != null) {
            return r;
        }

        Double credit = user.getCredit() + value;
        Double exp = user.getExp() + Math.abs(value);

        String userName = user.getName();

        // update record
        ObjectId objectId = getObjectIdAndSaveRecord(creditConstant, value, userName);

        // execute
        ResponseEntity executeError = execute(user, data, objectId);
        if (executeError != null) {
            return executeError;
        }

        // update user info
        updateUserInfo(credit, exp, userName);

        // log
        log(value, user.getName(), creditConstant.getMsg(data));
        return getCreditResponse(credit, exp);
    }

    private void log(Double value, String name, String msg) {
        AbstractCreditCalculator.logger.info("用户：{} 积分变动:{} 原因:{}", name, value, msg);
    }

    private ObjectId getObjectIdAndSaveRecord(
            CreditConstant creditConstant, Double value, String userName) {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        UserRecord userRecord = new UserRecord(date, creditConstant.getMsg(data), value, userName);
        mongoTemplate.insert(userRecord, "user_record");
        return userRecord.getId();
    }

    ResponseEntity getResponseEntity(HashMap data) {
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);
    }

    /**
     * Execute the service with id.
     *
     * @param id       just id param
     * @param objectId Record id. The spider will use it to set record status.
     */
    ResponseEntity execute(Long id, ObjectId objectId) {
        throw new BusinessException(ExceptionEnum.EXECUTE_FAILURE);
    }

    /**
     * Execute the service with user.
     *
     * @param user     just id user
     * @param objectId Record id. The spider will use it to set record status.
     */
    ResponseEntity execute(User user, ObjectId objectId) {
        throw new BusinessException(ExceptionEnum.EXECUTE_FAILURE);
    }

    ResponseEntity execute(User user, T data, ObjectId objectId) {
        throw new BusinessException(ExceptionEnum.EXECUTE_FAILURE);
    }

    void setExecuted(ObjectId objectId) {
        // update execute status
        Query query = new Query(where("_id").is(objectId));
        Update update = new Update();
        update.set("isExecuted", true);
        mongoTemplate.updateFirst(query, update, "user_record");
    }
}
