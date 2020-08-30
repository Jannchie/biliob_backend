package com.jannchie.biliob.credit.handle;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Jannchie
 */
@Component
public class CreditOperateHandle {
    private static final Logger logger = LogManager.getLogger();
    final MongoTemplate mongoTemplate;

    public CreditOperateHandle(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private void log(String name, Double value, String msg) {
        logger.info("用户：{} 积分变动:{} 原因:{}", name, value, msg);
    }

    private void updateUserInfo(Double credit, Double exp, String userName) {
        Query query = new Query(where("name").is(userName));
        Update update = new Update();
        update.set("credit", new BigDecimal(credit).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        update.set("exp", new BigDecimal(exp).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        mongoTemplate.updateFirst(query, update, User.class);
    }


    public <T> Result<T> doCreditOperate(CreditConstant creditConstant, Execution<T> execution) {
        User user = UserUtils.getFullInfo();

        return doCreditOperate(user, creditConstant, execution);
    }

    public <T> Result<T> doCreditOperate(CreditConstant creditConstant, String param, Execution<T> execution) {
        User user = UserUtils.getFullInfo();
        return doCreditOperate(user, creditConstant, param, execution);
    }


    public <T> Result<T> doCreditOperate(User user, CreditConstant creditConstant, Execution<T> execution) {
        UserRecord userRecord = getUserRecord(user, creditConstant);
        T data = execution.execute();
        saveUserRecord(userRecord, creditConstant.getMsg());
        Result<T> result = updateUserInfo(user, creditConstant.getValue());
        result.setData(data);
        log(user.getName(), creditConstant.getValue(), creditConstant.getMsg());
        return result;
    }

    public <T> Result<T> doCustomCreditOperate(User user, Double cost, CreditConstant creditConstant, String param, Execution<T> execution) {
        UserRecord userRecord = getUserRecord(user, creditConstant, -cost);
        userRecord.setExecuted(true);
        T data = execution.execute();
        saveUserRecord(userRecord, creditConstant.getMsg(param));
        Result<T> result = updateUserInfoWithOutExp(user, -cost);
        result.setData(data);
        log(user.getName(), -cost, creditConstant.getMsg(param));
        return result;
    }

    public <T> Result<T> doCustomCreditOperate(User user, Double cost, CreditConstant creditConstant, Execution<T> execution) {
        UserRecord userRecord = getUserRecord(user, creditConstant, -cost);
        userRecord.setExecuted(true);
        T data = execution.execute();
        saveUserRecord(userRecord, creditConstant.getMsg());
        Result<T> result = updateUserInfoWithOutExp(user, -cost);
        result.setData(data);
        log(user.getName(), -cost, creditConstant.getMsg());
        return result;
    }

    private <T> Result<T> updateUserInfoWithOutExp(User user, double value) {
        double credit = BigDecimal.valueOf(user.getCredit() + value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        double exp = user.getExp();
        String userName = user.getName();
        Query query = new Query(where("name").is(userName));
        Update update = new Update();
        update.set("credit", credit);
        mongoTemplate.updateFirst(query, update, User.class);
        return new Result<>(ResultEnum.SUCCEED, credit, exp);
    }


    public <T> Result<T> doCreditOperate(User user, CreditConstant creditConstant, String param, Execution<T> execution) {
        UserRecord userRecord = getUserRecord(user, creditConstant);
        T data = execution.execute();
        saveUserRecord(userRecord, creditConstant.getMsg(param));
        Result<T> result = updateUserInfo(user, creditConstant.getValue());
        result.setData(data);
        log(user.getName(), creditConstant.getValue(), creditConstant.getMsg(param));
        return result;
    }

    private void saveUserRecord(UserRecord userRecord, String msg) {
        userRecord.setExecuteTime(Calendar.getInstance().getTime());
        userRecord.setMessage(msg);
        mongoTemplate.save(userRecord);
    }

    private UserRecord getUserRecord(User user, CreditConstant creditConstant, Double credit) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserName(user.getName());
        userRecord.setExecuted(true);
        userRecord.setCredit(credit);
        userRecord.setDatetime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userRecord.setCreateTime(Calendar.getInstance().getTime());
        userRecord.setMessage(creditConstant.getMsg());
        return userRecord;
    }

    private UserRecord getUserRecord(User user, CreditConstant creditConstant) {
        return getUserRecord(user, creditConstant, creditConstant.getValue());
    }

    private <T> Result<T> updateUserInfo(User user, Double value) {
        double credit = BigDecimal.valueOf(user.getCredit() + value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        double exp = BigDecimal.valueOf(user.getExp() + Math.abs(value)).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        String userName = user.getName();
        Query query = new Query(where("name").is(userName));
        Update update = new Update();
        update.set("credit", credit);
        update.set("exp", exp);
        mongoTemplate.updateFirst(query, update, User.class);
        return new Result<>(ResultEnum.SUCCEED, credit, exp);
    }

    public <T> Result<T> doAsyncCreditOperate(User user, CreditConstant creditConstant, Execution<T> execution) {
        T data = execution.execute();
        Result<T> result = updateUserInfo(user, creditConstant.getValue());
        result.setData(data);
        log(user.getName(), creditConstant.getValue(), creditConstant.getMsg());
        return result;
    }


    @FunctionalInterface
    public interface Execution<Q> {

        /**
         * Execute
         *
         * @return data
         */
        Q execute();
    }
}
