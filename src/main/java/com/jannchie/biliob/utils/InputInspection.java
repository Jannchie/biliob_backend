package com.jannchie.biliob.utils;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author jannchie
 */
@Component
public class InputInspection {
    private static final String ID_PATTERN = "^[\\d]{1,12}$";
    private static Pattern idPattern = Pattern.compile(ID_PATTERN);

    public static boolean isId(String str) {
        return str != null && !Objects.equals(str, "0") && idPattern.matcher(str).matches();
    }
}
