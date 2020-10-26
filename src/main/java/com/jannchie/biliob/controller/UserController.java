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
import com.jannchie.biliob.utils.BiliobUtils;
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
    @Autowired
    BiliobUtils biliobUtils;
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    public UserController(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/activation-code")
    public ResponseEntity<?> getActivationCode(@RequestParam @Valid String mail) {
        logger.info("[{}]: 向邮箱[{}]发送验证码", biliobUtils.getUserName(), mail);
        return userService.sendActivationCode(mail);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user")
    public ResponseEntity<?> createUser(@RequestBody @Valid Map<String, String> requestMap) {
        logger.info("创建观测者账号，名为[{}]", requestMap.get("name"));
        return userService.createUser(
                requestMap.get("name"),
                requestMap.get("password"),
                requestMap.get("mail"),
                requestMap.get("activationCode"));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/mail")
    public Result<?> bindMail(@RequestBody @Valid Map<String, String> requestMap) {
        logger.info("绑定邮箱[{}]", requestMap.get("name"));
        return userService.bindMail(
                requestMap.get("mail"),
                requestMap.get("activationCode"));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/author")
    public ResponseEntity<?> addFavoriteAuthor(@RequestBody @Valid Long mid)
            throws UserAlreadyFavoriteAuthorException {
        logger.info("添加喜欢的作者，mid: [{}]", mid);
        return userService.addFavoriteAuthor(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/video")
    public ResponseEntity<?> addFavoriteVideo(@RequestBody @Valid Long aid)
            throws UserAlreadyFavoriteVideoException {
        logger.info("添加喜欢的视频，aid: [{}]", aid);
        return userService.addFavoriteVideo(aid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user")
    public ResponseEntity<?> getUserInfo(@RequestParam(defaultValue = "old") String ver) {
        logger.info("获取观测者自身信息");
        userService.setVersion(ver);
        return userService.getUserInfo();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video")
    public Slice<?> getFavoriteVideo(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        logger.info("获得喜欢的视频分片，页{}，页大小{}", page, pageSize);
        return userService.getFavoriteVideo(page, pageSize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/author")
    public Slice<?> getFavoriteAuthor(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        logger.info("获得喜欢的作者分片，页{}，页大小{}", page, pageSize);
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
            logger.info("观测者[{}]登录成功", user.getName());
            return ResponseEntity.ok(new Result<>(ResultEnum.LOGIN_SUCCEED, user));
        } catch (AuthenticationException e) {
            logger.info("观测者登录失败");
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
    public ResponseEntity<?> deleteFavoriteVideo(@PathVariable("aid") Long aid) {
        logger.info("移除喜欢的视频，aid: [{}]", aid);
        return userService.deleteFavoriteVideoByAid(aid);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/author/{mid}")
    public ResponseEntity<?> deleteFavoriteAuthor(@PathVariable("mid") Long mid) {
        logger.info("移除喜欢的UP主，mid: [{}]", mid);
        return userService.deleteFavoriteAuthorByMid(mid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/check-in")
    public Result<?> postCheckIn() {
        logger.info("签到");
        return userService.postCheckIn();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/check-in")
    public ResponseEntity<?> getCheckIn() {
        logger.info("获取签到状态");
        return userService.getCheckIn();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/status")
    public Result<?> forceFocus(
            @RequestParam(defaultValue = "false") @Valid Boolean forceFocus,
            @PathVariable("mid") @Valid Long mid) {
        logger.info("锁定mid: [{}]的更新频率", mid);
        return userService.forceFocus(mid, forceFocus);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/question")
    public Result<?> postQuestion(@RequestBody @Valid Question question) {
        logger.info("提出问题：[{}]", question.getQuestion());
        return userService.postQuestion(question.getQuestion());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/author/{mid}/data")
    public Result<?> refreshAuthor(@PathVariable("mid") @Valid Long mid) {
        logger.info("刷新作者数据，mid[{}]", mid);
        return userService.refreshAuthor(mid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/name")
    public Result<?> modifyUserName(@RequestParam(defaultValue = "", value = "name") String name) {
        logger.info("修改用户名，改为[{}]", name);
        return userService.modifyUserName(name);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/AV{aid}/data")
    public Result<?> refreshVideo(@PathVariable("aid") @Valid Long aid) {
        logger.info("刷新视频数据，aid: [{}]", aid);
        return userService.refreshVideo(aid);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/BV{bvid}/data")
    public Result<?> refreshVideo(@PathVariable("bvid") @Valid String bvid) {
        logger.info("刷新视频数据，bvid: [{}]", bvid);
        return userService.refreshVideo(bvid);
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/data")
    public Result<?> refreshVideo(@RequestBody Video video) {
        logger.info("刷新视频数据（通用接口）");
        if (video.getBvid() != null) {
            return userService.refreshVideo(video.getBvid());
        }
        if (video.getAid() != null) {
            return userService.refreshVideo(video.getAid());
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/rank/user")
    public ResponseEntity<?> userRank(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer pagesize) {
        logger.info("列出观测者的排名");
        return new ResponseEntity<>(userService.sliceUserRank(page, pagesize), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/record")
    public ResponseEntity<?> userRecord() {
        logger.info("列出观测者的操作记录");
        return new ResponseEntity<>(userService.getUserRecentRecord(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/video/{aid}/danmaku")
    public ResponseEntity<?> danmakuAggregate(@PathVariable("aid") @Valid Long aid) {
        logger.info("发起弹幕分析，aid: [{}]", aid);
        return userService.danmakuAggregate(aid);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/video/{aid}/data")
    public ResponseEntity<?> videoObserveAlterFrequency(
            @PathVariable("aid") @Valid Long aid,
            @RequestParam(defaultValue = "1") @Valid Integer timeDurationFlag) {
        logger.info("修改观测频率aid: [{}], flag: [{}]", aid, timeDurationFlag);
        return userService.videoObserveAlterFrequency(aid, timeDurationFlag);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/author/{mid}/data")
    public ResponseEntity<?> authorObserveAlterFrequency(
            @PathVariable("mid") @Valid Long mid,
            @RequestParam(defaultValue = "1") @Valid Integer typeFlag) {
        logger.info("修改观测频率mid: [{}], flag: [{}]", mid, typeFlag);
        return userService.authorObserveAlterFrequency(mid, typeFlag);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video/keyword")
    public Map<?, ?> getUserPreferKeyWord() {
        logger.info("获取观测者自身喜欢的关键词");
        return userService.getUserPreferKeyword();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/user/video/recommend")
    public ArrayList<?> getUserPreferVideoByFavoriteVideo(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        logger.info("根据观测者收藏的视频，获取观测者喜欢的视频");
        return userService.getUserPreferVideoByFavoriteVideo(page, pagesize);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/nickname")
    public Result<?> changeNickName(@RequestBody @Valid NickNameForm user) {
        logger.info("修改昵称为[{}]", user.getNickName());
        return userService.changeNickName(user.getNickName());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/mail")
    public Result<?> changeMail(@RequestBody @Valid ChangeMailForm changeMailForm) {
        logger.info("修改邮箱为[{}]", changeMailForm.getMail());
        return userService.changeMail(changeMailForm.getMail());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/password")
    public ResponseEntity<Result<String>> changeMail(@RequestBody @Valid ChangePasswordForm changePasswordForm) {
        logger.info("修改密码");
        return userService.changePassword(changePasswordForm);
    }

}
