package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum PageSizeEnum {
    /**
     * BIG_SIZE: The max page size for most of table in main contain. SMALL_SIZE: The max page size
     * for list in aside contain
     */
    SMALL_SIZE(5),
    BIG_SIZE(20),
    USER_RANK_SIZE(51);

    private Integer value;

    PageSizeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
