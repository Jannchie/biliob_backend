package com.jannchie.biliob.constant;

/**
 * @author Jannchie
 */

public enum AuthorAchievementEnum {
    /**
     * level: 等级
     * name: 名称
     * desc: 描述
     */
    REDUCE_IN_DAY_LV9(4001, 9, "末日降临", "单日粉丝数减少超过 %,d", 200000L),
    REDUCE_IN_DAY_LV8(4002, 8, "雪崩来袭", "单日粉丝数减少超过 %,d", 100000L),
    REDUCE_IN_DAY_LV7(4003, 7, "断崖下跌", "单日粉丝数减少超过 %,d", 50000L),

    CONTINUE_REDUCE_LV9(5001, 9, "互联网也有记忆", "连续掉粉超过 %,d 天", 365L),
    CONTINUE_REDUCE_LV8(5002, 8, "细水长流", "连续掉粉超过 %,d 天", 180L),
    CONTINUE_REDUCE_LV7(5003, 7, "不忍直视", "连续掉粉超过 %,d 天", 30L),


    INCREASE_LV5(2001, 5, "人生巅峰", "单日涨粉达到前日的 %,d 倍", 30L),
    INCREASE_LV4(2002, 4, "尖峰时刻", "单日涨粉达到前日的 %,d 倍", 10L),
    INCREASE_LV3(2003, 3, "一战成名", "单日涨粉达到前日的 %,d 倍", 5L),

    INCREASE_IN_DAY_LV10(2004, 10, "传说级涨粉", "单日涨粉达到 %,d", 300000L),
    INCREASE_IN_DAY_LV9(2005, 9, "史诗级涨粉", "单日涨粉达到 %,d", 200000L),
    INCREASE_IN_DAY_LV8(2006, 8, "大量涨粉", "单日涨粉达到 %,d", 100000L),

    NEW_STAR(3100, 8, "新星爆发", "粉丝数少于300,000且单日涨幅大于100,000"),
    UP_TO_DOWN(4004, 8, "急转直下", "前日涨粉超过1,000，后日掉粉超过1,000");


    private String name;
    private Integer id;
    private Integer level;
    private String desc;
    private Long value;

    AuthorAchievementEnum(Integer id, Integer level, String name, String desc, Long value) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.desc = String.format(desc, value);
        this.value = value;
    }

    AuthorAchievementEnum(Integer id, Integer level, String name, String desc) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.desc = desc;
        this.value = 0L;
    }

    public static AuthorAchievementEnum getById(Integer id) {
        for (AuthorAchievementEnum a : AuthorAchievementEnum.values()
        ) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
