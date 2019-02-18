package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

/**
 * @author jannchie
 */
@JsonInclude(Include.NON_NULL)
public class User {
	@Id
	private ObjectId id;

	@NotBlank(message = "用户ID不能为空")
  @Length(max = 16, message = "账号最长为16位")
  private String name;

	@Length(min = 6, message = "密码至少为6位")
	@NotBlank(message = "用户密码不能为空!")
	private String password;

	private String role;
	private ArrayList<Long> favoriteAid;
	private ArrayList<Long> favoriteMid;
  private Integer credit;
  private Integer exp;

  @Field("record")
  private ArrayList<UserRecord> recordArrayList;

  public ArrayList<UserRecord> getRecordArrayList() {
    return recordArrayList;
  }

  public void setRecordArrayList(ArrayList<UserRecord> recordArrayList) {
    this.recordArrayList = recordArrayList;
  }



	public User(String name, String password, String role) {
		this.name = name;
		this.password = password;
		this.role = role;
    this.credit = 0;
    this.exp = 0;
  }

	public User() {
		this.role = "普通用户";
    this.credit = 0;
    this.exp = 0;
  }

  public Integer getExp() {
    return exp;
  }

  public void setExp(Integer exp) {
    this.exp = exp;
  }

  public Integer getCredit() {
    return credit;
  }

  public void setCredit(Integer credit) {
    this.credit = credit;
  }

  public String getName() {
    return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ArrayList<Long> getFavoriteMid() {
		return favoriteMid;
	}

	public void setFavoriteMid(ArrayList<Long> favoriteMid) {
		this.favoriteMid = favoriteMid;
	}

	public ArrayList<Long> getFavoriteAid() {
		return favoriteAid;
	}

	public void setFavoriteAid(ArrayList<Long> favoriteAid) {
		this.favoriteAid = favoriteAid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
