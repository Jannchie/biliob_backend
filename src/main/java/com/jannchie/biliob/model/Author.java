package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;

/** @author jannchie */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {

  private Long mid;
  private String name;
  private String face;
  private String sex;
  private String official;
  private Integer level;
  private ArrayList<Data> data;
  private ArrayList<Channel> channels;
  private Rank rank;
  private Boolean focus;
  private Boolean forceFocus;
  private Integer cRate;
  private ArrayList<FansRate> fansRate;
  private ArrayList<String> keyword;

  public Author() {
    focus = true;
  }

  public Author(Long mid) {
    this.mid = mid;
    focus = true;
  }

  public ArrayList<String> getKeyword() {
    return keyword;
  }

  public Rank getRank() {
    return rank;
  }

  public ArrayList<FansRate> getFansRate() {
    return fansRate;
  }

  public void setFansRate(ArrayList<FansRate> fansRate) {
    this.fansRate = fansRate;
  }

  public Integer getcRate() {
    return cRate;
  }

  public void setcRate(Integer cRate) {
    this.cRate = cRate;
  }

  public Boolean getForceFocus() {
    return forceFocus;
  }

  public void setForceFocus(Boolean forceFocus) {
    this.forceFocus = forceFocus;
  }

  public ArrayList<Channel> getChannels() {
    return channels;
  }

  public void setChannels(ArrayList<Channel> channels) {
    this.channels = channels;
  }

  public void setChannel(ArrayList<Channel> channels) {
    this.channels = channels;
  }

  public Long getMid() {
    return mid;
  }

  public void setMid(Long mid) {
    this.mid = mid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFace() {
    return face;
  }

  public void setFace(String face) {
    this.face = face;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getOfficial() {
    return official;
  }

  public void setOfficial(String official) {
    this.official = official;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public ArrayList<Data> getData() {
    return data;
  }

  public void setData(ArrayList<Data> data) {
    this.data = data;
  }

  public Boolean getFocus() {
    return focus;
  }

  public void setFocus(Boolean focus) {
    this.focus = focus;
  }

  private static class Rank {
    private Integer fansRank;
    private Integer archiveViewRank;
    private Integer articleViewRank;
    private Integer dFansRank;
    private Integer dArchiveViewRank;
    private Integer dArticleViewRank;
    private Double pFansRank;
    private Double pArchiveViewRank;
    private Double pArticleViewRank;
    private Date updateTime;

    public Double getpFansRank() {
      return pFansRank;
    }

    public Double getpArchiveViewRank() {
      return pArchiveViewRank;
    }

    public Double getpArticleViewRank() {
      return pArticleViewRank;
    }

    public Integer getdFansRank() {
      return dFansRank;
    }

    public Integer getdArchiveViewRank() {
      return dArchiveViewRank;
    }

    public Integer getdArticleViewRank() {
      return dArticleViewRank;
    }

    public Date getUpdateTime() {
      return updateTime;
    }

    public Integer getFansRank() {
      return fansRank;
    }

    public Integer getArchiveViewRank() {
      return archiveViewRank;
    }

    public Integer getArticleViewRank() {
      return articleViewRank;
    }
  }

  private static class Data {
    private Integer fans;
    private Integer attention;
    private Integer archive;
    private Integer article;
    private Long archiveView;
    private Long articleView;
    private Date datetime;

    public Long getArchiveView() {
      return archiveView;
    }

    public void setArchiveView(Long archiveView) {
      this.archiveView = archiveView;
    }

    public Long getArticleView() {
      return articleView;
    }

    public void setArticleView(Long articleView) {
      this.articleView = articleView;
    }

    public Integer getFans() {
      return fans;
    }

    public void setFans(Integer fans) {
      this.fans = fans;
    }

    public Integer getAttention() {
      return attention;
    }

    public void setAttention(Integer attention) {
      this.attention = attention;
    }

    public Integer getArchive() {
      return archive;
    }

    public void setArchive(Integer archive) {
      this.archive = archive;
    }

    public Integer getArticle() {
      return article;
    }

    public void setArticle(Integer article) {
      this.article = article;
    }

    public Date getDatetime() {
      return datetime;
    }

    public void setDatetime(Date datetime) {
      this.datetime = datetime;
    }
  }

  private static class Channel {
    private Integer tid;
    private Integer count;
    private String name;

    public Integer getTid() {
      return tid;
    }

    public void setTid(Integer tid) {
      this.tid = tid;
    }

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  private class FansRate {
    private Integer rate;
    private Date datetime;

    public Integer getRate() {
      return rate;
    }

    public void setRate(Integer rate) {
      this.rate = rate;
    }

    public Date getDatetime() {
      return datetime;
    }

    public void setDatetime(Date datetime) {
      this.datetime = datetime;
    }
  }
}
