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
  ARCHIVE_VIEW(1, "cArchiveView"),
  ARTICLE_VIEW(2, "cArticleView");

  private Integer flag;
  private String key;

  AuthorSortEnum(Integer flag, String key) {
    this.flag = flag;
    this.key = key;
  }

  public static String getKeyByFlag(Integer flag) {
    switch (flag) {
      case 0:
        return FANS.getKey();
      case 1:
        return ARCHIVE_VIEW.getKey();
      case 2:
        return ARTICLE_VIEW.getKey();
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
