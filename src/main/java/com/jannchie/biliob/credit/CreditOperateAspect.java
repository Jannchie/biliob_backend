package com.jannchie.biliob.credit;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 此为爬虫调度器的切片。
 * 功能为防止同一爬虫调度任务多次执行。
 *
 * @author Pan Jianqi
 */
@Aspect
@Component
public class CreditOperateAspect {
    private static final Logger logger = LogManager.getLogger();
    private final MongoTemplate mongoTemplate;
    private ArrayList<String> executing = new ArrayList<>();

    public CreditOperateAspect(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Pointcut(value = "execution(public * com.jannchie.biliob.credit.handle.CreditOperateHandle.*(com.jannchie.biliob.model.User,com.jannchie.biliob.constant.CreditConstant,..))")
    public void checkCredit() {
    }


    @Around(value = "checkCredit() && args(user, creditConstant, ..)", argNames = "user,creditConstant")
    public Object doAround(ProceedingJoinPoint pjp, User user, CreditConstant creditConstant) throws Throwable {
        Double value = creditConstant.getValue();
        if (user == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        } else if (value < 0 && user.getCredit() < (-value)) {
            logger.info("用户：{},积分不足,当前积分：{}", user.getName(), user.getCredit());
            return new Result<>(ResultEnum.CREDIT_NOT_ENOUGH);
        }

        return pjp.proceed();
    }

}