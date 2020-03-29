package com.jannchie.biliob.constant;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Jannchie
 */

public enum AuthorUniqueAchievementEnum {
    /**
     * level: 等级
     * name: 名称
     * desc: 描述
     */
    GET_FANS_LV01(3001, 1, "破千", "粉丝数达到 %,d", 1000L),
    GET_FANS_LV02(3002, 2, "破万", "粉丝数达到 %,d", 10000L),
    GET_FANS_LV03(3003, 3, "知名UP主", "粉丝数达到 %,d", 100000L),
    GET_FANS_LV07(3004, 7, "迈向殿堂", "粉丝数达到 %,d", 1000000L),
    GET_FANS_LV08(3005, 8, "一曲史诗", "粉丝数达到 %,d", 5000000L),
    GET_FANS_LV09(3006, 9, "成为传说", "粉丝数达到 %,d", 10000000L),
    GET_FANS_LV10(3007, 10, "B站之光", "粉丝数达到 %,d", 100000000L),

    GET_PLAY_LV02(3011, 2, "十万播放", "播放数达到 %,d", 100000L),
    GET_PLAY_LV03(3012, 3, "百万播放", "播放数达到 %,d", 1000000L),
    GET_PLAY_LV04(3013, 4, "千万播放", "播放数达到 %,d", 10000000L),
    GET_PLAY_LV08(3014, 8, "一亿播放", "播放数达到 %,d", 100000000L),
    GET_PLAY_LV09(3015, 9, "十亿播放", "播放数达到 %,d", 1000000000L),
    GET_PLAY_LV10(3016, 10, "百亿播放", "播放数达到 %,d", 10000000000L),

    GET_LIKE_LV02(3021, 2, "万人点赞", "点赞数达到 %,d", 10000L),
    GET_LIKE_LV03(3022, 3, "十万点赞", "点赞数达到 %,d", 100000L),
    GET_LIKE_LV04(3023, 4, "百万点赞", "点赞数达到 %,d", 1000000L),
    GET_LIKE_LV08(3024, 8, "千万点赞", "点赞数达到 %,d", 10000000L),
    GET_LIKE_LV10(3025, 10, "一亿点赞", "点赞数达到 %,d", 100000000L);


    private String name;
    private Integer id;
    private Integer level;
    private String desc;
    private Long value;

    AuthorUniqueAchievementEnum(Integer id, Integer level, String name, String desc, Long value) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.desc = String.format(desc, value);
        this.value = value;
    }


    AuthorUniqueAchievementEnum(Integer id, Integer level, String name, String desc) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.desc = desc;
        this.value = 0L;
    }

    public static HashSet<Integer> getIdSet() {
        HashSet<Integer> result = new HashSet<>();
        Arrays.stream(AuthorUniqueAchievementEnum.values()).forEach(e -> {
            result.add(e.getId());
        });
        return result;
    }

    public static AuthorUniqueAchievementEnum getById(Integer id) {
        for (AuthorUniqueAchievementEnum a : AuthorUniqueAchievementEnum.values()
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
