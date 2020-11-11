package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.DbFields;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;

/**
 * @author Jannchie
 */
@Service
public class CreditService {
    private static final Logger logger = LogManager.getLogger(CreditService.class);
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserUtils userUtils;

    @Autowired
    public CreditService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperationWithoutExp(CreditConstant creditConstant, String message, Double credit) {
        return doCreditOperation(userUtils.getUser(), creditConstant, message, true, false, credit);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperationFansGuessing(User user, CreditConstant creditConstant, String message, Double credit) {
        return doCreditOperation(user, creditConstant, message, true, false, credit);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(CreditConstant creditConstant) {
        return this.doCreditOperation(creditConstant, creditConstant.getMsg(), true);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(CreditConstant creditConstant, String message) {
        return this.doCreditOperation(creditConstant, message, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(CreditConstant creditConstant, String message, Boolean isExecuted) {
        User user = userUtils.getUser();
        return doCreditOperation(user, creditConstant, message, isExecuted);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(User user, CreditConstant creditConstant, String message) {
        return doCreditOperation(user, creditConstant, message, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(User user, CreditConstant creditConstant, String message, Boolean isExecuted) {
        return doCreditOperation(user, creditConstant, message, isExecuted, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(User user, CreditConstant creditConstant, String message, Boolean isExecuted, Boolean withExp) {
        return doCreditOperation(user, creditConstant, message, isExecuted, withExp, creditConstant.getValue());
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperation(User user, CreditConstant creditConstant, String message, Boolean isExecuted, Boolean withExp, Double credit) {
        if (user == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.info("用户为NULL");
            return ResultEnum.HAS_NOT_LOGGED_IN.getCreditResult();
        }
        if (user.getCredit() < -credit) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.info("积分不足");
            return ResultEnum.CREDIT_NOT_ENOUGH.getCreditResult();
        }
        if (user.getBan() != null && user.getBan()) {
            return ResultEnum.BANNED.getCreditResult();
        }
        user.setCredit(BigDecimal.valueOf(user.getCredit()).add(BigDecimal.valueOf(credit)).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        double exp = 0D;
        if (withExp) {
            exp = Math.abs(credit);
            user.setExp(BigDecimal.valueOf(user.getExp()).add(BigDecimal.valueOf(exp)).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        }
        mongoTemplate.update(User.class).matching(Query.query(Criteria.where(DbFields.ID).is(user.getId()))).apply(Update.update("credit", user.getCredit()).set("exp", user.getExp())).first();
        UserRecord ur = new UserRecord(user, creditConstant, message, isExecuted);
        ur.setCredit(credit);
        ur = mongoTemplate.save(ur);
        if (!isExecuted) {
            Result<T> r = ResultEnum.ACCEPTED.getResult(user);
            r.setUserRecord(ur);
            return r;
        }
        logger.info("观测者[{}]: {} [exp:{}(+{}), cre:{}({})]", user.getName(), message, user.getExp(), exp, user.getCredit(), credit);
        return ResultEnum.SUCCEED.getResult(user);
    }
}
