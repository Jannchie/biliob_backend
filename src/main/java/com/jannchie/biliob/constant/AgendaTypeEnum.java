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
    ENHANCE((byte) 1),
    FEATURE((byte) 2),
    FIX((byte) 3),
    OTHER((byte) 4);
    private Byte value;

    AgendaTypeEnum(Byte i) {
        this.value = i;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }
}
