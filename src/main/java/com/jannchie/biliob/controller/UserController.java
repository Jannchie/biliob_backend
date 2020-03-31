package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.form.ChangeMailForm;
import com.jannchie.biliob.form.ChangePasswordForm;
import com.jannchie.biliob.model.Question;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.LoginForm;
import com.jannchie.biliob.object.NickNameForm;
import com.jannchie.biliob.security.UserAuthenticationProvider;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.Message;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author jannchie
 */
@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    public UserController(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/activation-code")
    public ResponseEntity getActivationCode(@RequestParam @Valid String mail) {
        return userService.sendActivationCode(mail);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user")
    public ResponseEntity createUser(@RequestBody @Valid Map<String, String> requestMap) {
        return userService.createUser(
                requestMap.get("name"),
                requestMap.get("password"),
                requestMap.get("mail"),
                requestMap.get("activationCode"));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/mail")
    public ResponseEntity bindMail(@RequestBody @Valid Map<String, String> requestMap) {
        return userService.bindMail(
                requestMap.get("mail"),
                requestMap.get("activationCode"));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/author")
    public ResponseEntity addFavoriteAuthor(@RequestBody @Valid Long mid)
            throws UserAlreadyFavoriteAuthorException {
        return userService.addFavoriteAuthor(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/video")
    public ResponseEntity addFavoriteVideo(@RequestBody @Valid Long aid)
            throws UserAlreadyFavoriteVideoException {
        return userService.addFavoriteVideo(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user")
    public ResponseEntity getUserInfo() {
        return userService.getUserInfo();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video")
    public Slice<?> getFavoriteVideo(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return userService.getFavoriteVideo(page, pageSize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author")
    public Slice<?> getFavoriteAuthor(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return userService.getFavoriteAuthor(page, pageSize);
    }

    @RequestMapping(
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST},
            value = "/api/no-login"
    )
    public ResponseEntity<Message> noLogin() {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        return new ResponseEntity<>(new Message(403, "未登录"), headers, HttpStatus.FORBIDDEN);
    }

    @RequestMapping(
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST},
            value = "/api/no-rule"
    )
    public ResponseEntity<Message> noRule() {
        return new ResponseEntity<>(new Message(403, "未授权"), HttpStatus.FORBIDDEN);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/login")
    public ResponseEntity<Result<User>> login(@Valid @RequestBody LoginForm data) {
        try {
            User user = getSignedUser(data);
            logger.info("用户[{}]登录成功", user.getName());
            return ResponseEntity.ok(new Result<>(ResultEnum.LOGIN_SUCCEED, user));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new Result<>(ResultEnum.LOGIN_FAILED));
        }
    }

    private User getSignedUser(@RequestBody @Valid LoginForm data) {
        Authentication request = new UsernamePasswordAuthenticationToken(data.getName(),
                data.getPassword());
        Authentication result = userAuthenticationProvider.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(result);
        return UserUtils.getUser();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/video/{aid}")
    public ResponseEntity deleteFavoriteVideo(@PathVariable("aid") @Valid Long aid) {
        return userService.deleteFavoriteVideoByAid(aid);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/author/{mid}")
    public ResponseEntity deleteFavoriteAuthor(@PathVariable("mid") @Valid Long mid) {
        return userService.deleteFavoriteAuthorByMid(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/check-in")
    public ResponseEntity postCheckIn() {
        return userService.postCheckIn();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/check-in")
    public ResponseEntity getCheckIn() {
        return userService.getCheckIn();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/status")
    public ResponseEntity forceFocus(
            @RequestParam(defaultValue = "false") @Valid Boolean forceFocus,
            @PathVariable("mid") @Valid Long mid) {
        return userService.forceFocus(mid, forceFocus);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/question")
    public ResponseEntity postQuestion(@RequestBody @Valid Question question) {
        return userService.postQuestion(question.getQuestion());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/data")
    public ResponseEntity refreshAuthor(@PathVariable("mid") @Valid Long mid) {
        return userService.refreshAuthor(mid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/name")
    public ResponseEntity modifyUserName(@RequestParam(defaultValue = "", value = "name") @Valid String name) {
        return userService.modifyUserName(name);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/AV{aid}/data")
    public ResponseEntity<?> refreshVideo(@PathVariable("aid") @Valid Long aid) {
        return userService.refreshVideo(aid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/BV{bvid}/data")
    public ResponseEntity<?> refreshVideo(@PathVariable("bvid") @Valid String bvid) {
        return userService.refreshVideo(bvid);
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/data")
    public ResponseEntity refreshVideo(@RequestBody Video video) {
        if (video.getBvid() != null) {
            return userService.refreshVideo(video.getBvid());
        }
        if (video.getAid() != null) {
            return userService.refreshVideo(video.getAid());
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/user")
    public ResponseEntity userRank(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer pagesize) {
        return new ResponseEntity<>(userService.sliceUserRank(page, pagesize), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/record")
    public ResponseEntity userRecord() {
        return new ResponseEntity<>(userService.getUserAllRecord(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/{aid}/danmaku")
    public ResponseEntity danmakuAggregate(@PathVariable("aid") @Valid Long aid) {
        return userService.danmakuAggregate(aid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/video/{aid}/data")
    public ResponseEntity videoObserveAlterFrequency(
            @PathVariable("aid") @Valid Long aid,
            @RequestParam(defaultValue = "1") @Valid Integer timeDurationFlag) {
        return userService.videoObserveAlterFrequency(aid, timeDurationFlag);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/author/{mid}/data")
    public ResponseEntity authorObserveAlterFrequency(
            @PathVariable("mid") @Valid Long mid,
            @RequestParam(defaultValue = "1") @Valid Integer typeFlag) {
        return userService.authorObserveAlterFrequency(mid, typeFlag);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video/keyword")
    public Map getUserPreferKeyWord() {
        return userService.getUserPreferKeyword();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video/recommend")
    public ArrayList getUserPreferVideoByFavoriteVideo(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        return userService.getUserPreferVideoByFavoriteVideo(page, pagesize);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/nickname")
    public ResponseEntity<Result<String>> changeNickName(@RequestBody @Valid NickNameForm user) {
        return userService.changeNickName(user.getNickName());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/mail")
    public ResponseEntity<Result<String>> changeMail(@RequestBody @Valid ChangeMailForm changeMailForm) {
        return userService.changeMail(changeMailForm.getMail());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/password")
    public ResponseEntity<Result<String>> changeMail(@RequestBody @Valid ChangePasswordForm changePasswordForm) {
        return userService.changePassword(changePasswordForm);
    }
}
