package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jannchie
 */
@Component
public class BiliobUtils {
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


    public String getUserName() {
        User user = UserUtils.getUser();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        } else {
            userName = IpUtil.getIpAddress(this.request);
        }
        return userName;
    }

    public Map getVisitData(String userName, Long mid) {
        Date date = Calendar.getInstance().getTime();
        Map data = new HashMap<String, Object>() {
            {
                put("mid", mid);
                put("name", userName);
                put("ip", IpUtil.getIpAddress(request));
                put("user-agent", request.getHeader("User-Agent"));
                put("date", date);
            }
        };
        return data;
    }
}
