package com.jannchie.biliob.constant;

/**
 * @author Jannchie
 */

public enum AgendaState {
    /**
     * WAITING: 等待被处理
     * FINISHED: 已经处理完毕
     * CLOSED: 不会处理此议题
     * PENDING：正在处理此议题
     * DUPLICATE：该议题重复
     */
    WAITING((byte) 0),
    FINISHED((byte) 1),
    CLOSED((byte) 2),
    PENDING((byte) 3),
    DUPLICATE((byte) 4);

    private final Byte value;

    AgendaState(Byte i) {
        this.value = i;
    }

    public Byte getValue() {
        return value;
    }
}
