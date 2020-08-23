package com.jannchie.biliob.constant;

/**
 * @author Jannchie
 */

public enum AgendaTypeEnum {
    /**
     * ENHANCE: 增强现有的功能
     * FEATURE: 增加新的功能
     * FIX: 修复错误
     * OTHER: 其他
     */
    ENHANCE(1),
    FEATURE(2),
    FIX(3),
    OTHER(4);
    private Integer value;

    AgendaTypeEnum(Integer i) {
        this.value = i;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
