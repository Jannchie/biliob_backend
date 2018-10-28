package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import javax.validation.Valid;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody @Valid User user) {

        String inputName = user.getName();
        String inputPassword = user.getPassword();
        String encodedPassword = new Md5Hash(inputPassword, inputName).toHex();

        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(inputName, encodedPassword);
        // 执行认证登陆
        subject.login(token);
        //根据权限，指定返回数据
        String role = userService.getRole(inputName);
        if ("普通用户".equals(role)) {
            return new ResponseEntity<>("欢迎登陆",HttpStatus.OK);
        }
        return new ResponseEntity<>("登录失败",HttpStatus.OK);
    }
}
