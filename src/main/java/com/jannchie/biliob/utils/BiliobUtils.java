package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.DbFields;
import com.jannchie.biliob.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Jannchie
 */
@Component
public class BiliobUtils {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private HttpServletRequest request;

    public BiliobUtils(HttpServletRequest request) {
        this.request = request;
    }

    public static String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    @Cacheable(value = "getNameToNickNameMap", key = "#userIdSet")
    public Map<String, String> getNameToNickNameMap(Collection<ObjectId> userIdSet) {
        Query qq = Query.query(Criteria.where(DbFields.ID).in(userIdSet));
        qq.fields().include(DbFields.NICKNAME).include(DbFields.NAME);
        Map<String, String> nameToNickNameMap = new HashMap<>(100);
        mongoTemplate.find(qq, User.class).forEach(user -> nameToNickNameMap.put(user.getName(), user.getNickName()));
        return nameToNickNameMap;
    }

    public String getUserName() {
        String userName = UserUtils.getUsername();
        if (userName == null) {
            userName = IpUtil.getIpAddress(this.request);
        }
        return userName;
    }

    public Map<?, ?> getVisitData(String userName, Long mid) {
        Date date = Calendar.getInstance().getTime();
        return new HashMap<String, Object>(10) {
            {
                put("mid", mid);
                put("name", userName);
                put("ip", IpUtil.getIpAddress(request));
                put("user-agent", request.getHeader("User-Agent"));
                put("date", date);
            }
        };
    }
}
