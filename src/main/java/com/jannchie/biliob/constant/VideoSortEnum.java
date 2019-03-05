package com.jannchie.biliob.constant;

/**
 * @author jannchie
 */
public enum VideoSortEnum {

  /**
   * video sort enum
   */
  VIEW(0, "cView"),
  LIKE(1, "cLike"),
  COIN(2, "cCoin"),
  DANMAKU(3, "cDanmaku"),
  FAVORITE(4, "cFavorite"),
  SHARE(5, "cShare");

  private Integer flag;
  private String key;

  VideoSortEnum(Integer flag, String key) {
    this.flag = flag;
    this.key = key;
  }

  public static String getKeyByFlag(Integer flag) {
    switch (flag) {
      case 0:
        return VIEW.getKey();
      case 1:
        return LIKE.getKey();
      case 2:
        return COIN.getKey();
      case 3:
        return DANMAKU.getKey();
      case 4:
        return FAVORITE.getKey();
      case 5:
        return SHARE.getKey();
      default:
        return VIEW.getKey();
    }
  }

  public Integer getFlag() {
    return flag;
  }

  public String getKey() {
    return key;
  }
}
