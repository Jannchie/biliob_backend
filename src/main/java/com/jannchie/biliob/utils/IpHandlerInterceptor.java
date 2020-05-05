package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Blacklist;
import com.jannchie.biliob.model.IpVisitRecord;
import com.jannchie.biliob.model.UserAgentBlackList;
import com.jannchie.biliob.model.WhiteList;
import com.jannchie.biliob.object.IpAggregationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Double CHECK_RATE = 0.05D;
    private final MongoTemplate mongoTemplate;

    /**
     * controller 执行之前调用
     */
    @Autowired
    public IpHandlerInterceptor(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public static String replaceDigital(String value) {
        Matcher matcher = NUMBER_PATTERN.matcher(value);
        return matcher.replaceAll("{id}");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String ip = IpUtil.getIpAddress(request);
        String userAgent = request.getHeader("user-agent");
        String uri = replaceDigital(request.getRequestURI());

        // 在白名单中直接放过
        if (mongoTemplate.exists(query(where(IP).is(ip)), WhiteList.class)) {
            return true;
        }
        // 在黑名单中直接处决
        if (mongoTemplate.exists(query(where(IP).is(ip)), Blacklist.class)) {
            returnJson(response);
            return false;
        }

        if (isBot(userAgent)) {
            addToBlackList(response, ip, String.format("使用爬虫 %s", userAgent));
        }


        // 保存一条IP访问记录
        mongoTemplate.save(new IpVisitRecord(ip, userAgent, uri));


        if (Math.random() < CHECK_RATE) {
            List<IpAggregationInfo> ipAggregationInfoList = mongoTemplate.aggregate(Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("ip").is(ip)),
                    Aggregation.group("uri").first("ip").as("ip").count().as("count")
            ), IpVisitRecord.class, IpAggregationInfo.class).getMappedResults();
            for (IpAggregationInfo info : ipAggregationInfoList
            ) {
                // 如果某个API在十分钟内访问300次
                if (info.getCount() > 300) {
                    addToBlackList(response, ip, String.format("访问频率过高 %s", info.getUri()));
                    return false;
                }
            }
        }
        return true;
    }

    private void addToBlackList(HttpServletResponse response, String ip, String reason) {
        mongoTemplate.save(new Blacklist(ip, String.format("%s", reason)));
        logger.info(ip);
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    private boolean isBot(String userAgent) {
        List<String> banedUserAgent = mongoTemplate.findAll(UserAgentBlackList.class).stream().map(UserAgentBlackList::getUserAgent).collect(Collectors.toList());
        for (String ua : banedUserAgent
        ) {
            if (userAgent.contains(ua)) {
                return true;
            }
        }
        return false;
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
