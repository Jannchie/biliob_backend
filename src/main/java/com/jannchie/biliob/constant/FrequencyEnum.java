package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum FrequencyEnum {

    /**
     * video sort enum
     */
    ONE_HOUR_LAST_7_DAYS(1, "一小时更新一次，持续一周"),
    TWO_HOUR_LAST_14_DAYS(2, "两小时更新一次，持续两周");

    private Integer flag;
    private String key;

    FrequencyEnum(Integer flag, String key) {
        this.flag = flag;
        this.key = key;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getKey() {
        return key;
    }
}
