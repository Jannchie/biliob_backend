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

/**
 * @author Jannchie
 */
@Service
public class CreditService {
    private static final Logger logger = LogManager.getLogger(CreditService.class);
    private MongoTemplate mongoTemplate;

    @Autowired
    public CreditService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperationWithoutExp(CreditConstant creditConstant, String message, Double credit) {
        return doCreditOperation(UserUtils.getUser(), creditConstant, message, true, false, credit);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> Result<T> doCreditOperationFansGuessing(CreditConstant creditConstant, String message, Double credit) {
        return doCreditOperation(UserUtils.getUser(), creditConstant, message, true, false, credit);
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
        User user = UserUtils.getUser();
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
            return ResultEnum.HAS_NOT_LOGGED_IN.getCreditResult();
        }
        if (user.getCredit() < -credit) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultEnum.CREDIT_NOT_ENOUGH.getCreditResult();
        }
        logger.info("观测者[{}]: {}", user.getName(), message);
        user.setCredit(user.getCredit() + credit);
        if (withExp) {
            if (credit < 0) {
                user.setExp(user.getExp() - credit);
            } else {
                user.setExp(user.getExp() + credit);
            }
        }
        mongoTemplate.update(User.class).matching(Query.query(Criteria.where(DbFields.ID).is(user.getId()))).apply(Update.update("credit", user.getCredit()).set("exp", user.getExp())).first();
        UserRecord ur = mongoTemplate.save(new UserRecord(user, creditConstant, message, isExecuted));
        if (!isExecuted) {
            Result<T> r = ResultEnum.ACCEPTED.getResult(user);
            r.setUserRecord(ur);
            return r;
        }
        return ResultEnum.SUCCEED.getResult(user);
    }
}
