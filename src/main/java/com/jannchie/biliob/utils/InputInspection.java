package com.jannchie.biliob.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author jannchie
 */
@Component
public class InputInspection {
  private static final String ID_PATTERN = "^[\\d]*$";
  private static Pattern idPattern = Pattern.compile(ID_PATTERN);

  public static boolean isId(String str) {
    return idPattern.matcher(str).matches();
  }
}
