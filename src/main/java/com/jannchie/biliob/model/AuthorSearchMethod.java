package com.jannchie.biliob.model;

/**
 * @author jannchie
 */
public class AuthorSearchMethod {
  private String owner;
  private String name;
  private String orderBy;
  private String sort;
  private String bucket;
  private String groupByField;
  private String groupReference;
  private String groupKeyword;
  private String matchField;
  private String matchValue;
  private String matchMethod;
  private Integer bucketType;
  private Integer day;

  public AuthorSearchMethod() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getGroupByField() {
    return groupByField;
  }

  public void setGroupByField(String groupByField) {
    this.groupByField = groupByField;
  }

  public String getGroupReference() {
    return groupReference;
  }

  public void setGroupReference(String groupReference) {
    this.groupReference = groupReference;
  }

  public String getGroupKeyword() {
    return groupKeyword;
  }

  public void setGroupKeyword(String groupKeyword) {
    this.groupKeyword = groupKeyword;
  }

  public String getMatchField() {
    return matchField;
  }

  public void setMatchField(String matchField) {
    this.matchField = matchField;
  }

  public String getMatchValue() {
    return matchValue;
  }

  public void setMatchValue(String matchValue) {
    this.matchValue = matchValue;
  }

  public String getMatchMethod() {
    return matchMethod;
  }

  public void setMatchMethod(String matchMethod) {
    this.matchMethod = matchMethod;
  }

  public Integer getBucketType() {
    return bucketType;
  }

  public void setBucketType(Integer bucketType) {
    this.bucketType = bucketType;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }
}
