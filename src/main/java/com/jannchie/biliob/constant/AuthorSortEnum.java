package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum AuthorSortEnum {

    /**
     * FANS: author's fans flag and keyword. ARTICLE_VIEW: author's article view flag and keyword.
     * ARCHIVE_VIEW: author's archive view flag and keyword.
     */
    FANS(0, "cFans"),
    ARCHIVE_VIEW(1, "cArchive_view"),
    ARTICLE_VIEW(2, "cArticle_view"),
    LIKE(3, "cLike");

    private Integer flag;
    private String key;


    AuthorSortEnum(Integer flag, String key) {
        this.flag = flag;
        this.key = key;
    }

    public static String getKeyByFlag(Integer flag) {
        switch (flag) {
            case 1:
                return ARCHIVE_VIEW.getKey();
            case 2:
                return ARTICLE_VIEW.getKey();
            case 3:
                return LIKE.getKey();
            default:
                return FANS.getKey();
        }
    }

    public Integer getFlag() {
        return flag;
    }

    public String getKey() {
        return key;
    }
}
