package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.FieldConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.credit.handle.CreditHandle;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.form.ChangePasswordForm;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.Question;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.repository.*;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.*;
import com.jannchie.biliob.utils.credit.calculator.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;

import static com.jannchie.biliob.constant.PageSizeEnum.BIG_SIZE;
import static com.jannchie.biliob.constant.PageSizeEnum.USER_RANK_SIZE;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Service
class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final CreditUtil creditUtil;

    private final UserRepository userRepository;

    private final VideoRepository videoRepository;

    private final AuthorRepository authorRepository;

    private final UserRecordRepository userRecordRepository;

    private final QuestionRepository questionRepository;

    private final MongoTemplate mongoTemplate;

    private final RefreshAuthorCreditCalculator refreshAuthorCreditCalculator;

    private final RefreshVideoCreditCalculator refreshVideoCreditCalculator;

    private final DanmakuAggregateCreditCalculator danmakuAggregateCreditCalculator;

    private final CheckInCreditCalculator checkInCreditCalculator;

    private final ForceFocusCreditCalculator forceFocusCreditCalculator;
    private final AuthorService authorService;

    private final MailUtil mailUtil;
    private final RecommendVideo recommendVideo;
    private CreditHandle creditHandle;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private AuthorUtil authorUtil;
    private CreditOperateHandle creditOperateHandle;

    @Autowired
    public UserServiceImpl(
            CreditUtil creditUtil,
            UserRepository userRepository,
            VideoRepository videoRepository,
            AuthorRepository authorRepository,
            QuestionRepository questionRepository,
            UserRecordRepository userRecordRepository,
            MongoTemplate mongoTemplate,
            RefreshAuthorCreditCalculator refreshAuthorCreditCalculator,
            RefreshVideoCreditCalculator refreshVideoCreditCalculator,
            ForceFocusCreditCalculator forceFocusCreditCalculator,
            DanmakuAggregateCreditCalculator danmakuAggregateCreditCalculator,
            CheckInCreditCalculator checkInCreditCalculator,
            ModifyNickNameCreditCalculator modifyNickNameCreditCalculator,
            AuthorService authorService, CreditHandle creditHandle,
            MailUtil mailUtil,
            RecommendVideo recommendVideo, AuthorUtil authorUtil, CreditOperateHandle creditOperateHandle) {
        this.creditUtil = creditUtil;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.authorRepository = authorRepository;
        this.questionRepository = questionRepository;
        this.userRecordRepository = userRecordRepository;
        this.mongoTemplate = mongoTemplate;
        this.authorService = authorService;
        this.creditHandle = creditHandle;
        this.refreshAuthorCreditCalculator = refreshAuthorCreditCalculator;
        this.forceFocusCreditCalculator = forceFocusCreditCalculator;
        this.refreshVideoCreditCalculator = refreshVideoCreditCalculator;
        this.danmakuAggregateCreditCalculator = danmakuAggregateCreditCalculator;
        this.checkInCreditCalculator = checkInCreditCalculator;
        this.mailUtil = mailUtil;
        this.recommendVideo = recommendVideo;
        this.authorUtil = authorUtil;
        this.creditOperateHandle = creditOperateHandle;
    }

    @Override
    public ResponseEntity<Result<?>> createUser(
            String username, String password, String mail, String activationCode) {
        User user = new User(username, password, RoleEnum.LEVEL_1.getName());
        if (!mailUtil.checkActivationCode(mail, activationCode)) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.ACTIVATION_CODE_UNMATCHED), HttpStatus.BAD_REQUEST);
        }
        // activation code unmatched
        if (1 == userRepository.countByName(user.getName())) {
            // 已存在同名
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.USER_ALREADY_EXIST), HttpStatus.BAD_REQUEST);
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(mail)), "user")) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED), HttpStatus.NOT_ACCEPTABLE);
        }

        user.setName(user.getName());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setNickName(user.getName());
        user.setMail(mail);
        user.setCredit(0D);
        user.setExp(0D);
        user.setRole("普通用户");
        userRepository.save(user);
        UserServiceImpl.logger.info(user.getName());
        // 不要返回密码
        user.setPassword(null);
        return new ResponseEntity<>(new Result<>(ResultEnum.SUCCEED, user), HttpStatus.OK);
    }

    @Override
    public String getPassword(String name) throws UserNotExistException {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new UserNotExistException(name);
        }
        return userRepository.findByName(name).getPassword();
    }

    @Override
    public String getRole(String name) {
        return userRepository.findByName(name).getRole();
    }

    @Override
    public ResponseEntity<?> getUserInfo() {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }

        UserUtils.setUserTitleAndRankAndUpdateRole(user);
        UserServiceImpl.logger.info(user.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> addFavoriteAuthor(@Valid Long mid) {
        User user = UserUtils.getFullInfo();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        ArrayList<Long> temp = new ArrayList<>();
        if (user.getFavoriteMid() != null) {
            temp = user.getFavoriteMid();
        }
        if (temp.contains(mid)) {
            UserServiceImpl.logger.warn("用户：{} 试图重复关注{}", user.getName(), mid);
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.ALREADY_FAVORITE_AUTHOR), HttpStatus.ACCEPTED);
        }
        temp.add(mid);
        user.setFavoriteMid(new ArrayList<>(temp));
        userRepository.save(user);
        UserServiceImpl.logger.info("用户：{} 关注了{}", user.getName(), mid);
        return new ResponseEntity<>(new Result<>(ResultEnum.ADD_FAVORITE_AUTHOR_SUCCEED), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addFavoriteVideo(@Valid Long aid) {
        User user = UserUtils.getFullInfo();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        ArrayList<Long> temp = new ArrayList<>();
        if (user.getFavoriteAid() != null) {
            temp = user.getFavoriteAid();
        }
        if (temp.contains(aid)) {
            UserServiceImpl.logger.warn("用户：{} 试图重复收藏{}", user.getName(), aid);
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.ALREADY_FAVORITE_VIDEO), HttpStatus.ACCEPTED);
        }
        temp.add(aid);
        user.setFavoriteAid(new ArrayList<>(temp));
        userRepository.save(user);
        UserServiceImpl.logger.info("用户：{} 关注了{}", user.getName(), aid);
        return new ResponseEntity<>(new Result<>(ResultEnum.ADD_FAVORITE_VIDEO_SUCCEED), HttpStatus.OK);
    }

    /**
     * Get user's favorite video page
     *
     * @param page     page number
     * @param pageSize page size
     * @return favorite video page
     */
    @Override
    public Slice getFavoriteVideo(Integer page, Integer pageSize) {
        if (pageSize > BIG_SIZE.getValue()) {
            pageSize = BIG_SIZE.getValue();
        }
        User user = UserUtils.getUser();
        if (user == null) {
            return null;
        }
        if (user.getFavoriteAid() == null) {
            return null;
        }
        ArrayList<Long> aids = user.getFavoriteAid();
        ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
        for (Long aid : aids) {
            HashMap<String, Long> temp = new HashMap<>(1);
            temp.put("aid", aid);
            mapsList.add(temp);
        }
        UserServiceImpl.logger.info(user.getName());
        return videoRepository.getFavoriteVideo(mapsList, PageRequest.of(page, pageSize));
    }

    /**
     * Get user's favorite author page
     *
     * @param page     page number
     * @param pageSize page size
     * @return favorite author page
     */
    @Override
    public Slice<?> getFavoriteAuthor(Integer page, Integer pageSize) {
        if (pageSize > BIG_SIZE.getValue()) {
            pageSize = BIG_SIZE.getValue();
        }
        User user = UserUtils.getUser();
        if (user == null) {
            return null;
        }
        if (user.getFavoriteMid() == null) {
            return null;
        }
        ArrayList<Long> mids = user.getFavoriteMid();
        ArrayList<HashMap<String, Long>> mapsList = new ArrayList<>();
        for (Long mid : mids) {
            HashMap<String, Long> temp = new HashMap<>(1);
            temp.put("mid", mid);
            mapsList.add(temp);
        }
        UserServiceImpl.logger.info(user.getName());
        Slice<Author> authors = authorRepository.getFavoriteAuthor(mapsList, PageRequest.of(page, pageSize));
        authorUtil.getInterval(authors.getContent());
        return authors;
    }

    /**
     * delete user's favorite author by author id
     *
     * @param mid author's id
     * @return response with message
     */
    @Override
    public ResponseEntity<?> deleteFavoriteAuthorByMid(Long mid) {
        User user = UserUtils.getFullInfo();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        ArrayList<Long> mids = user.getFavoriteMid();
        for (int i = 0; i < mids.size(); i++) {
            if (Objects.equals(mids.get(i), mid)) {
                mids.remove(i);
                user.setFavoriteMid(mids);
                userRepository.save(user);
                UserServiceImpl.logger.info("删除[{}]关注的UP主：{}", user.getName(), mid);
                return new ResponseEntity<>(new Result<>(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
            }
        }
        UserServiceImpl.logger.warn("用户：{} 试图删除一个不存在的UP主", user.getName());
        return new ResponseEntity<>(new Result<>(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    /**
     * delete user's favorite video by video id
     *
     * @param aid video's id
     * @return response with message
     */
    @Override
    public ResponseEntity<?> deleteFavoriteVideoByAid(Long aid) {
        User user = UserUtils.getFullInfo();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        ArrayList<Long> aids = user.getFavoriteAid();
        for (int i = 0; i < aids.size(); i++) {
            if (Objects.equals(aids.get(i), aid)) {
                aids.remove(i);
                user.setFavoriteAid(aids);
                userRepository.save(user);
                UserServiceImpl.logger.info("用户：{} 删除了收藏的视频，aid：{}", user.getName(), aid);
                return new ResponseEntity<>(new Result<>(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
            }
        }
        UserServiceImpl.logger.warn("用户：{} 试图删除一个不存在的视频", user.getName());
        return new ResponseEntity<>(new Result<>(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> login(String name, String passwd) {
        User user =
                mongoTemplate.findOne(
                        Query.query(
                                new Criteria()
                                        .orOperator(Criteria.where("name").is(name), Criteria.where("mail").is(name))),
                        User.class,
                        "user");
        if (user == null) {
            return new ResponseEntity<>(new Result<String>(ResultEnum.LOGIN_FAILED), HttpStatus.UNAUTHORIZED);
        }
        String inputName = user.getName();
        String encodedPassword = new Md5Hash(passwd, inputName).toHex();
        Subject subject = SecurityUtils.getSubject();

        User tempUser = userRepository.findByName(inputName);
        if (tempUser == null) {
            return new ResponseEntity<>(new Result<>(ResultEnum.LOGIN_FAILED), HttpStatus.UNAUTHORIZED);
        }

        if (tempUser.getPassword() == null) {
            tempUser.setPassword(encodedPassword);
            userRepository.save(tempUser);
        }

        UsernamePasswordToken token = new UsernamePasswordToken(inputName, encodedPassword);
        token.setRememberMe(true);
        subject.login(token);
        String role = getRole(inputName);
        UserServiceImpl.logger.info("{}：{} 登录成功", role, inputName);
        return new ResponseEntity<>(new Result<>(ResultEnum.LOGIN_SUCCEED, getUserInfo()), HttpStatus.OK);
    }

    /**
     * user can check in and get credit every eight hour.
     *
     * @return check in response
     */
    @Override
    public ResponseEntity<?> postCheckIn() {
        return checkInCreditCalculator.executeAndGetResponse(CreditConstant.CHECK_IN);
    }

    /**
     * to know whether user is checked in
     *
     * @return check in status
     */
    @Override
    public ResponseEntity<?> getCheckIn() {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.OK);
        }
        Boolean isCheckedIn =
                mongoTemplate.exists(new Query(where("name").is(user.getName())), "check_in");
        HashMap<String, Boolean> statusHashMap = new HashMap<>(1);
        statusHashMap.put("status", isCheckedIn);
        UserServiceImpl.logger.info("用户：{},签到状态为{}", user.getName(), isCheckedIn);
        return new ResponseEntity<>(statusHashMap, HttpStatus.OK);
    }

    /**
     * Force Focus a Author or Not.
     *
     * @param mid        author id
     * @param forceFocus force focus status
     * @return Force observation or cancel the force observation feedback.
     */
    @Override
    public ResponseEntity<?> forceFocus(Long mid, @Valid Boolean forceFocus) {
        return forceFocusCreditCalculator.executeAndGetResponse(CreditConstant.SET_FORCE_OBSERVE, mid);
    }

    /**
     * post a question
     *
     * @param question the question text
     * @return the post result.
     */
    @Override
    public ResponseEntity<?> postQuestion(String question) {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        String userName = user.getName();
        HashMap<String, Double> data = creditUtil.calculateCredit(user, CreditConstant.ASK_QUESTION);
        if (data.get(FieldConstant.CREDIT.getValue()) != -1) {
            questionRepository.save(new Question(question, userName));
            UserServiceImpl.logger.info("用户：{} 提出了一个问题：{}", user.getName(), question);
            return new ResponseEntity<>(new Result<>(ResultEnum.SUCCEED, data), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Result<>(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
        }
    }

    /**
     * Refresh author data immediately.
     *
     * @param mid author id
     * @return response
     */
    @Override
    public ResponseEntity<?> refreshAuthor(@Valid Long mid) {
        User u = UserUtils.getUser();
        UserRecord userRecord = mongoTemplate.insert(new UserRecord(CreditConstant.REFRESH_AUTHOR_DATA, String.valueOf(mid), u.getName()));
        Result<?> result = creditOperateHandle.doAsyncCreditOperate(u, CreditConstant.REFRESH_AUTHOR_DATA,
                () -> {
                    Query q = Query.query(Criteria.where("mid").is(mid));
                    if (!mongoTemplate.exists(q, AuthorIntervalRecord.class)) {
                        authorService.upsertAuthorFreq(mid, 86400);
                    }
                    return mongoTemplate.updateFirst(q,
                            new Update().addToSet("order", userRecord.getId()).set("next", new Date(0)), AuthorIntervalRecord.class);
                });
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> refreshVideo(@Valid Long aid) {
        User u = UserUtils.getUser();
        UserRecord userRecord = mongoTemplate.insert(new UserRecord(CreditConstant.REFRESH_VIDEO_DATA, String.valueOf(aid), u.getName()));
        Result<?> result = creditOperateHandle.doAsyncCreditOperate(u, CreditConstant.REFRESH_VIDEO_DATA,
                () -> mongoTemplate.updateFirst(Query.query(Criteria.where("aid").is(aid)),
                        new Update().addToSet("order", userRecord.getId()).set("next", new Date(0)), "video_interval"));
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> refreshVideo(@Valid String bvid) {
        User u = UserUtils.getUser();
        UserRecord userRecord = mongoTemplate.insert(new UserRecord(CreditConstant.REFRESH_VIDEO_DATA, bvid, u.getName()));
        Result<?> result = creditOperateHandle.doAsyncCreditOperate(u, CreditConstant.REFRESH_VIDEO_DATA,
                () -> mongoTemplate.updateFirst(Query.query(Criteria.where("bvid").is(bvid)),
                        new Update().addToSet("order", userRecord.getId()), "video_interval"));
        return ResponseEntity.ok(result);
    }

    /**
     * Rank of user, order by exp
     *
     * @param page     offset
     * @param pagesize number of element
     * @return the slice of user rank
     */
    @Override
    public MySlice<User> sliceUserRank(Integer page, Integer pagesize) {
        // max size is 100
        if (!pagesize.equals(USER_RANK_SIZE.getValue())) {
            pagesize = USER_RANK_SIZE.getValue();
        }

        Query q = new Query().with(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "exp")));
        // get user slice
        q.fields().include("exp").include("nickName").include("_id");

        List<User> s =
                mongoTemplate.find(q, User.class);
        s.sort(Comparator.comparing(User::getExp).reversed());
        return new MySlice<>(s);
    }

    /**
     * User starts a danmaku aggregate task.
     *
     * @param aid the video id being aggregated
     * @return the response
     */
    @Override
    public ResponseEntity<?> danmakuAggregate(@Valid Long aid) {
        return danmakuAggregateCreditCalculator.executeAndGetResponse(
                CreditConstant.DANMAKU_AGGREGATE, aid);
    }

    /**
     * slice the user record
     *
     * @param page     page number
     * @param pagesize page size
     * @return the slice of user record
     */
    @Override
    public MySlice<UserRecord> sliceUserRecord(Integer page, Integer pagesize) {
        pagesize = DataReducer.limitPagesize(pagesize);
        User user = UserUtils.getUser();
        if (user != null) {
            String userName = user.getName();
            Slice<UserRecord> slice =
                    userRecordRepository.findByUserNameOrderByDatetimeDesc(
                            userName, PageRequest.of(page, pagesize));
            return new MySlice<>(slice);
        } else {
            return null;
        }
    }

    /**
     * Get user's all records.
     *
     * @return user record array list
     */
    @Override
    public ArrayList<UserRecord> getUserAllRecord() {
        User user = UserUtils.getUser();
        if (user != null) {
            String userName = user.getName();
            return userRecordRepository.findAllByUserNameOrderByDatetimeDesc(userName);
        } else {
            return null;
        }
    }

    /**
     * video observe frequency alter
     *
     * @param aid      video id
     * @param typeFlag type flag
     * @return operation result
     */
    @Override
    public ResponseEntity<?> videoObserveAlterFrequency(@Valid Long aid, @Valid Integer typeFlag) {
        return null;
    }

    /**
     * author observe frequency alter
     *
     * @param mid      video id
     * @param typeFlag type flag
     * @return operation result
     */
    @Override
    public ResponseEntity<?> authorObserveAlterFrequency(@Valid Long mid, @Valid Integer typeFlag) {
        return null;
    }

    /**
     * modify user's name
     *
     * @param newUserName new user name
     * @return operation result
     */
    @Override
    public ResponseEntity<Result<String>> modifyUserName(@Valid String newUserName) {
        return creditHandle.modifyUserName(UserUtils.getUser(), CreditConstant.MODIFY_NAME, newUserName);
    }

    /**
     * Get activation code
     *
     * @param mail user's email
     * @return operation result
     */
    @Override
    public ResponseEntity<?> sendActivationCode(@Valid String mail) {
        return mailUtil.sendActivationCode(mail);
    }

    @Override
    public Map<String, Integer> getUserPreferKeyword() {
        User user = UserUtils.getUser();
        if (user == null) {
            return null;
        }
        ArrayList<Long> aidList = user.getFavoriteAid();
        Map<String, Integer> result = new HashMap<>(aidList.size());
        for (Long eachAid : aidList) {
            result.put(String.valueOf(eachAid), 1);
        }
        return recommendVideo.getKeyWordMapFromAidCountMap(result);
    }

    /**
     * get user prefer video by user favorite videos
     *
     * @return video list
     */
    @Override
    public ArrayList getUserPreferVideoByFavoriteVideo(Integer page, Integer pagesize) {
        return recommendVideo.getRecommendVideoByTagCountMap(
                this.getUserPreferKeyword(), page, pagesize);
    }


    @Override
    public ResponseEntity<?> bindMail(String mail, String activationCode) {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        if (!mailUtil.checkActivationCode(mail, activationCode)) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.ACTIVATION_CODE_UNMATCHED), HttpStatus.BAD_REQUEST);
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(mail)), User.class)) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED), HttpStatus.UNAUTHORIZED);
        }

        return creditHandle.modifyMail(UserUtils.getUser(), CreditConstant.MODIFY_MAIL, mail);
    }

    @Override
    public ResponseEntity<Result<String>> changeNickName(String newNickname) {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        if (newNickname.length() > 20) {
            return new ResponseEntity<>(new Result<>(ResultEnum.OUT_OF_RANGE), HttpStatus.BAD_REQUEST);
        }
        return creditHandle.modifyUserName(UserUtils.getUser(), CreditConstant.MODIFY_NAME, newNickname);
    }

    @Override
    public ResponseEntity<Result<String>> changeMail(String newMail) {
        User user = UserUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(newMail)), User.class)) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED), HttpStatus.UNAUTHORIZED);
        }
        user.setMail(newMail);
        return creditHandle.modifyMail(UserUtils.getUser(), CreditConstant.MODIFY_MAIL, newMail);
    }

    @Override
    public ResponseEntity<Result<String>> changePassword(ChangePasswordForm changePasswordForm) {
        if (!mailUtil.checkActivationCode(changePasswordForm.getMail(), changePasswordForm.getActivationCode())) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.ACTIVATION_CODE_UNMATCHED), HttpStatus.BAD_REQUEST);
        }
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("mail").is(changePasswordForm.getMail())),
                Update.update("password", bCryptPasswordEncoder.encode(changePasswordForm.getPassword())),
                User.class);
        return new ResponseEntity<>(
                new Result<>(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    @Override
    @Cacheable("user-count")
    public long getUserCount() {
        return mongoTemplate.count(new Query(), "user");
    }

    @Override
    public List<UserRecord> getUserRecentRecord() {
        User user = UserUtils.getUser();
        if (user != null) {
            String userName = user.getName();
            return mongoTemplate.find(Query.query(Criteria.where("userName").is(userName)).with(Sort.by("_id").descending()).limit(100), UserRecord.class);
        } else {
            return null;
        }
    }
}
