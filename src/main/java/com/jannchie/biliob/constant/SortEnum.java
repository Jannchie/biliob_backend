package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum SortEnum {
    /**
     * SIGN: Every eight hour, user can sign in once time.
     */
    VIEW_COUNT(0),
    PUBLISH_TIME(1);

    private Integer value;

    SortEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
