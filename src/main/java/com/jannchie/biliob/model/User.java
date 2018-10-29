package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@JsonInclude(Include.NON_NULL)
public class User {
    @Id
    private ObjectId id;

    @NotBlank(message = "用户ID不能为空")
    private String name;

    @Length(min = 6, message = "密码至少为6位")
    @NotBlank(message = "用户密码不能为空!")
    private String password;

<<<<<<<Updated upstream
    Stashed changespublic

    String getName() {
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
<<<<<<<Updated upstream
=======

    public User(ObjectId id, String name, String password) {
        this.id = id;
=======
        private String role;

        private ArrayList<Long> favoriteAid;

        private ArrayList<Long> favoriteMid;

    public User(String name, String password, String role) {
>>>>>>>Stashed changes
            this.name = name;
            this.password = password;
        }

    public User() {
        }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
>>>>>>>

    public void setFavoriteAid(ArrayList<Long> favoriteAid) {
        this.favoriteAid = favoriteAid;
    }
}
