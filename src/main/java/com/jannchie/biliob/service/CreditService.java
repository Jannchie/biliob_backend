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
    public Result<UserRecord> doCreditOperation(CreditConstant creditConstant) {
        return this.doCreditOperation(creditConstant, creditConstant.getMsg(), true);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<UserRecord> doCreditOperation(CreditConstant creditConstant, String message) {
        return this.doCreditOperation(creditConstant, message, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<UserRecord> doCreditOperation(CreditConstant creditConstant, String message, Boolean isExecuted) {
        User user = UserUtils.getUser();
        if (user == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultEnum.HAS_NOT_LOGGED_IN.getCreditResult();
        }
        Double credit = creditConstant.getValue();
        if (user.getCredit() < -credit) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultEnum.CREDIT_NOT_ENOUGH.getCreditResult();
        }
        logger.info("观测者[{}]: {}", user.getName(), message);
        user.setCredit(user.getCredit() + credit);
        if (credit < 0) {
            user.setExp(user.getExp() - credit);
        }
        mongoTemplate.update(User.class).matching(Query.query(Criteria.where(DbFields.ID).is(user.getId()))).apply(Update.update("credit", user.getCredit()).set("exp", user.getExp())).first();
        UserRecord ur = mongoTemplate.save(new UserRecord(user, creditConstant, message, isExecuted));
        if (!isExecuted) {
            return ResultEnum.ACCEPTED.getResult(ur, user);
        }
        return ResultEnum.SUCCEED.getResult(null, user);
    }
}
