package com.jannchie.biliob.utils.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class CheckExecutingAspect {
    private static final Logger logger = LogManager.getLogger();
    private ArrayList<String> executing = new ArrayList<>();

    @Pointcut("execution(public * com.jannchie.biliob.utils.schedule.*.*(..))")
    public void checkExecuting() {
    }

    @Around("checkExecuting()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        if (!executing.contains(methodName)) {

            executing.add(methodName);
            logger.debug("[START] {} {}", methodName, Thread.currentThread());
            return pjp.proceed();
        }
        return false;
    }

    @After("checkExecuting()")
    public void doAfter(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        if (executing.remove(methodName)) {
            logger.debug("[END] {} {}", methodName, Thread.currentThread());
        }
    }
}