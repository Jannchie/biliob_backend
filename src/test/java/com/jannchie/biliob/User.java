package com.jannchie.biliob;

import org.springframework.data.annotation.Id;

public class User {

    @Id
    private Long id;
    private String username;
    private Integer age;

    public User(Long id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

}
