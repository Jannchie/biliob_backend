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
    NORMAL_USER(0, "普通用户"),
    ADMIN(9, "管理员");

    private Integer code;
    private String name;

    RoleEnum(Integer code) {
        this.code = code;
    }

    RoleEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
