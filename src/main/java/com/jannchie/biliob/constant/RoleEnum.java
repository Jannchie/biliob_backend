package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum RoleEnum {
    /**
     * the role of the site.
     *
     * <p>NORMAL_USER: The user who already sign in.
     *
     * <p>ADMIN: The user who has the highest authority of this site.
     */
    GUEST(-1, "无权限游客"),
    LEVEL_1(0, "普通研究员"),
    LEVEL_2(4, "高级研究员"),
    LEVEL_3(5, "管理研究员"),
    LEVEL_4(7, "特权研究员"),
    LEVEL_5(8, "系统测试员"),
    LEVEL_6(9, "站长");

    private Integer level;
    private String name;

    RoleEnum(Integer level) {
        this.level = level;
    }

    RoleEnum(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    public static Integer getLevelByName(String name) {
        for (RoleEnum r : RoleEnum.values()
        ) {
            if (r.getName().equals(name)) {
                return r.getLevel();
            }
        }
        return 0;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer code) {
        this.level = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
