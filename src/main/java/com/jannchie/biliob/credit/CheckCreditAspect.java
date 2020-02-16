package com.jannchie.biliob.credit;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 此为爬虫调度器的切片。SW
 * 功能为防止同一爬虫调度任务多次执行。
 *
 * @author Pan Jianqi
 */
@Aspect
@Component
public class CheckCreditAspect {
    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;
    private ArrayList<String> executing = new ArrayList<>();

    public CheckCreditAspect(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Pointcut(value = "execution(public org.springframework.http.ResponseEntity com.jannchie.biliob.credit.handle.*.*(com.jannchie.biliob.model.User,com.jannchie.biliob.constant.CreditConstant,..))")
    public void checkCredit() {
    }

    private void log(Double value, String name, String msg) {
        logger.info("用户：{} 积分变动:{} 原因:{}", name, value, msg);
    }

    private void updateUserInfo(Double credit, Double exp, String userName) {
        Query query = new Query(where("name").is(userName));
        Update update = new Update();
        update.set("credit", credit);
        update.set("exp", exp);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Around(value = "checkCredit() && args(user, creditConstant, ..)", argNames = "pjp,user,creditConstant")
    public ResponseEntity<?> doAround(ProceedingJoinPoint pjp, User user, CreditConstant creditConstant) throws Throwable {
        Double value = creditConstant.getValue();
        if (user == null) {
            return new ResponseEntity<Result<?>>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        } else if (value < 0 && user.getCredit() < (-value)) {
            logger.info("用户：{},积分不足,当前积分：{}", user.getName(), user.getCredit());
            return new ResponseEntity<Result<?>>(new Result<>(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
        }

        Double credit = user.getCredit() + value;
        Double exp = user.getExp() + Math.abs(value);
        String userName = user.getName();
        Object o = pjp.proceed();
        if (o instanceof ResponseEntity) {
            ResponseEntity<?> res = ((ResponseEntity<?>) o);
            if (res.getBody() instanceof Result<?>) {
                Result<?> result = (Result<?>) res.getBody();
                if (result.getData() instanceof String) {
                    String data = (String) result.getData();
                    updateRecord(user, creditConstant, data);

                }
            }
            updateUserInfo(credit, exp, userName);
            return res;
        }
        return null;
    }


    private void updateRecord(User user, CreditConstant creditConstant, String msg) {
        // update record
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        UserRecord userRecord = new UserRecord(date, creditConstant.getMsg(msg), creditConstant.getValue(), user.getName(), true);
        mongoTemplate.insert(userRecord, "user_record");
    }
}