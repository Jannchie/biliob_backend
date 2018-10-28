package com.jannchie.biliob.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class User {
    @Id
    private ObjectId id;

    @NotBlank(message = "用户ID不能为空")
    private String name;

    @Length(min = 6, message = "密码至少为6位")
    @NotBlank(message = "用户密码不能为空!")
    private String password;

    private String role;

    public User(String name, String password,String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public User() {
        this.role = "普通用户";
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
