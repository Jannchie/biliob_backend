package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.User;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BiliOBUtils {
    public static String getUserName() {
        User user = LoginChecker.checkInfo();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        }
        return userName;
    }

    public static Map getVisitData(String userName, Long mid) {

        Date date = Calendar.getInstance().getTime();
        Map data = new HashMap<String, Object>() {
            {
                put("mid", mid);
                put("name", userName);
                put("date", date);
            }
        };
        return data;
    }
}
