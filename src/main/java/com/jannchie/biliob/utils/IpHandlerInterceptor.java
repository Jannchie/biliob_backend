package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Blacklist;
import com.jannchie.biliob.model.IpVisitRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author jannchie
 */
@Component
public class IpHandlerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(IpHandlerInterceptor.class);
    private static final Integer MAX_CUD_IN_MINUTE = 180;
    private static final Integer MAX_R_IN_MINUTE = 360;
    private static final String IP = "ip";
    private static final ArrayList<String> LOCALHOST = new ArrayList<>(Arrays.asList("0:0:0:0:0:0:0:1", "127.0.0.1"));

    private static HashMap<String, Integer> blackIP = new HashMap<>(256);
    private final MongoTemplate mongoTemplate;
    @Autowired
    public IpHandlerInterceptor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * controller 执行之前调用
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String ip = IpUtil.getIpAddress(request);

        // 在黑名单中直接处决，除非这次请求是本地发起的
        if (mongoTemplate.exists(query(where(IP).is(ip)), Blacklist.class) && !LOCALHOST.contains(ip)) {
            returnJson(response);
            return false;
        }

        // 保存一条IP访问记录
        mongoTemplate.save(new IpVisitRecord(ip));

        int limitCount = Integer.MAX_VALUE;
        if (Objects.equals(request.getMethod(), HttpMethod.DELETE.name())
                || Objects.equals(request.getMethod(), HttpMethod.POST.name())
                || Objects.equals(request.getMethod(), HttpMethod.PATCH.name())
                || Objects.equals(request.getMethod(), HttpMethod.PUT.name())) {
            // POST或PATCH或PUT或DELETE方法
            limitCount = MAX_CUD_IN_MINUTE;
        } else if (Objects.equals(request.getMethod(), HttpMethod.GET.name())) {
            // GET方法
            limitCount = MAX_R_IN_MINUTE;
        }
        if (blackIP.containsKey(ip)) {
            int newCount = blackIP.get(ip) + 1;
            blackIP.put(ip, newCount);
            if (newCount > limitCount) {
                mongoTemplate.insert(new Blacklist(ip), "blacklist");
                logger.info(ip);
                blackIP.remove(ip);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                returnJson(response);
                return false;
            }
        } else {
            blackIP.put(ip, 1);
        }
        return true;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() {
        blackIP = new HashMap<>(256);
    }

    private void returnJson(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print("{\"msg\":\"YOU HAVE BEEN CAUGHT.\"}");

        } catch (IOException e) {
            logger.error("response error", e);
        }
    }
}
