package com.jannchie.biliob.constant;

/**
 * @author Jannchie
 */

public enum UserOpinion {
    /**
     * 赞成、反对、弃权
     */
    IN_FAVOR((byte) 1),
    AGAINST((byte) 2),
    ABSTENTION((byte) 3);

    private final Byte value;

    UserOpinion(Byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }
}
