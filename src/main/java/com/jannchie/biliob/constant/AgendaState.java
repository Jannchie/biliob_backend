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
    WAITING(0),
    FINISHED(1),
    CLOSED(2),
    PENDING(3),
    DUPLICATE(4);

    private final int value;

    AgendaState(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
