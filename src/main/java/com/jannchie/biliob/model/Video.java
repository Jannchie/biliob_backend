package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** @author jannchie */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video {
  private Long aid;
  private Long mid;
  private String title;
  private String author;
  private String channel;
  private String subChannel;
  private Date datetime;
  private String pic;
  private Boolean focus;
  private ArrayList<Data> data;
  private ArrayList<String> keyword;
  private Rank rank;
  @Field("danmaku_aggregate")
  private HashMap<Object, Object> danmakuAggregate;

  public Video(Long aid) {
    this.aid = aid;
    this.focus = true;
  }

  public Video() {
    this.focus = true;
  }

  public ArrayList<String> getKeyword() {
    return keyword;
  }

  public Rank getRank() {
    return rank;
  }

  public Long getAid() {
    return aid;
  }

  public Long getMid() {
    return mid;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getChannel() {
    return channel;
  }

  public String getSubChannel() {
    return subChannel;
  }

  public String getPic() {
    return pic;
  }

  public ArrayList<Data> getData() {
    return data;
  }

  public Boolean getFocus() {
    return focus;
  }

  public Date getDatetime() {
    return datetime;
  }

  public HashMap<Object, Object> getDanmakuAggregate() {
    return danmakuAggregate;
  }

  private static class Rank {
    private Integer cViewRank;
    private Integer cLikeRank;
    private Integer cDanmakuRank;
    private Integer cFavoriteRank;
    private Integer cCoinRank;
    private Integer cShareRank;
    private Integer dViewRank;
    private Integer dLikeRank;
    private Integer dDanmakuRank;
    private Integer dFavoriteRank;
    private Integer dCoinRank;
    private Integer dShareRank;
    private Double pViewRank;
    private Double pLikeRank;
    private Double pDanmakuRank;
    private Double pFavoriteRank;
    private Double pCoinRank;
    private Double pShareRank;
    private Date updateTime;

    public Rank() {}

    public Double getpViewRank() {
      return pViewRank;
    }

    public Double getpLikeRank() {
      return pLikeRank;
    }

    public Double getpDanmakuRank() {
      return pDanmakuRank;
    }

    public Double getpFavoriteRank() {
      return pFavoriteRank;
    }

    public Double getpCoinRank() {
      return pCoinRank;
    }

    public Double getpShareRank() {
      return pShareRank;
    }

    public Integer getcViewRank() {
      return cViewRank;
    }

    public Integer getcLikeRank() {
      return cLikeRank;
    }

    public Integer getcDanmakuRank() {
      return cDanmakuRank;
    }

    public Integer getcFavoriteRank() {
      return cFavoriteRank;
    }

    public Integer getcCoinRank() {
      return cCoinRank;
    }

    public Integer getcShareRank() {
      return cShareRank;
    }

    public Integer getdViewRank() {
      return dViewRank;
    }

    public Integer getdLikeRank() {
      return dLikeRank;
    }

    public Integer getdDanmakuRank() {
      return dDanmakuRank;
    }

    public Integer getdFavoriteRank() {
      return dFavoriteRank;
    }

    public Integer getdCoinRank() {
      return dCoinRank;
    }

    public Integer getdShareRank() {
      return dShareRank;
    }

    public Date getUpdateTime() {
      return updateTime;
    }
  }

  public static class Data {
    private Integer view;
    private Integer favorite;
    private Integer danmaku;
    private Integer coin;
    private Integer share;
    private Integer like;
    private Integer dislike;
    private Date datetime;

    public Data() {}

    public Integer getView() {
      return view;
    }

    public Integer getFavorite() {
      return favorite;
    }

    public Integer getDanmaku() {
      return danmaku;
    }

    public Integer getCoin() {
      return coin;
    }

    public Integer getShare() {
      return share;
    }

    public Integer getLike() {
      return like;
    }

    public Integer getDislike() {
      return dislike;
    }

    public Date getDatetime() {
      return datetime;
    }
  }
}
