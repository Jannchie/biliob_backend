package com.jannchie.biliob.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.DbFields;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.constant.RoleEnum;
import com.jannchie.biliob.exception.UserNotExistException;
import com.jannchie.biliob.form.ChangePasswordForm;
import com.jannchie.biliob.model.*;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.object.VideoIntervalRecord;
import com.jannchie.biliob.repository.*;
import com.jannchie.biliob.service.CreditService;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static com.jannchie.biliob.constant.DbFields.FORCE_FOCUS;
import static com.jannchie.biliob.constant.PageSizeEnum.BIG_SIZE;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Service
class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private UserRecordRepository userRecordRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CreditService creditService;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private RecommendVideo recommendVideo;
    @Autowired
    private UserUtils userUtils;
    private AuthorUtil authorUtil;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

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
    public User getUserInfo() {
        User user = userUtils.getUser();
        if (user == null) {
            httpServletResponse.setStatus(400);
            return ResultEnum.HAS_NOT_LOGGED_IN.getResult();
        }
        String token = JWT.create()
                .withClaim("name", user.getName())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC256("jannchie"));
        userUtils.setUserTitleAndRankAndUpdateRole(user);
        httpServletResponse.setHeader("token", token);
        user.setIp(null);
        return user;
    }


    @Override
    public ResponseEntity<?> addFavoriteAuthor(@Valid Long mid) {
        User user = userUtils.getFullInfo();
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
        return new ResponseEntity<>(new Result<>(ResultEnum.ADD_FAVORITE_AUTHOR_SUCCEED), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addFavoriteVideo(@Valid Long aid) {
        User user = userUtils.getFullInfo();
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
    public Slice<?> getFavoriteVideo(Integer page, Integer pageSize) {
        if (pageSize > BIG_SIZE.getValue()) {
            pageSize = BIG_SIZE.getValue();
        }
        User user = userUtils.getUser();
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
        User user = userUtils.getUser();
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
        User user = userUtils.getFullInfo();
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
        User user = userUtils.getFullInfo();
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
                return new ResponseEntity<>(new Result<>(ResultEnum.DELETE_SUCCEED), HttpStatus.OK);
            }
        }
        UserServiceImpl.logger.warn("用户：{} 试图删除一个不存在的视频", user.getName());
        return new ResponseEntity<>(new Result<>(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.NOT_FOUND);
    }


    /**
     * user can check in and get credit every eight hour.
     *
     * @return check in response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> postCheckIn() {
        String name = userUtils.getUsername();
        if (name == null) {
            return ResultEnum.HAS_NOT_LOGGED_IN.getResult();
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("name").is(name)), CheckIn.class)) {
            return ResultEnum.ALREADY_SIGNED.getResult();
        }
        CheckIn checkIn = new CheckIn(name);
        mongoTemplate.insert(checkIn);
        return creditService.doCreditOperation(CreditConstant.CHECK_IN);
    }

    /**
     * to know whether user is checked in
     *
     * @return check in status
     */
    @Override
    public ResponseEntity<?> getCheckIn() {
        User user = userUtils.getUser();
        if (user == null) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.OK);
        }
        Boolean isCheckedIn =
                mongoTemplate.exists(new Query(where("name").in(user.getName(), user.getMail())), "check_in");
        HashMap<String, Boolean> statusHashMap = new HashMap<>(1);
        statusHashMap.put("status", isCheckedIn);
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
    @Transactional(rollbackFor = Exception.class)
    public Result<?> forceFocus(Long mid, @Valid Boolean forceFocus) {
        String msg = CreditConstant.SET_AUTHOR_FORCE_OBSERVE.getMsg(mid);

        Query q = Query.query(Criteria.where(DbFields.MID).is(mid));
        Author a = mongoTemplate.findOne(q, Author.class);

        if (a == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultEnum.AUTHOR_NOT_FOUND.getResult();
        }
        if (a.getForceFocus() == null) {
            a.setForceFocus(false);
        }
        if (a.getForceFocus().equals(forceFocus)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultEnum.ALREADY_FORCE_FOCUS.getResult();
        }
        mongoTemplate.update(Author.class).matching(q).apply(Update.update(FORCE_FOCUS, forceFocus)).first();
        return creditService.doCreditOperation(CreditConstant.SET_AUTHOR_FORCE_OBSERVE, msg);
    }

    /**
     * post a question
     *
     * @param question the question text
     * @return the post result.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> postQuestion(String question) {
        String msg = CreditConstant.SET_AUTHOR_FORCE_OBSERVE.getMsg();
        questionRepository.save(new Question(question, userUtils.getUsername()));
        return creditService.doCreditOperation(CreditConstant.SET_AUTHOR_FORCE_OBSERVE, msg);
    }

    /**
     * Refresh author data immediately.
     *
     * @param mid author id
     * @return response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> refreshAuthor(@Valid Long mid) {
        String msg = CreditConstant.REFRESH_AUTHOR_DATA.getMsg(mid);
        Result<?> result = creditService.doCreditOperation(CreditConstant.REFRESH_AUTHOR_DATA, msg, false);
        if (result.getCode() == -1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return result;
        }
        UserRecord ur = result.getUserRecord();
        Query q = Query.query(Criteria.where("mid").is(mid));
        mongoTemplate.upsert(q, new Update().addToSet("order", ur.getId()).set("next", new Date(0)), AuthorIntervalRecord.class);
        result.setData(null);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> refreshVideo(@Valid Long aid) {
        Query q = Query.query(Criteria.where("aid").is(aid));
        String msg = CreditConstant.REFRESH_VIDEO_DATA.getMsg(aid);
        return refreshVideo(q, msg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> refreshVideo(@Valid String bvid) {
        Query q = Query.query(Criteria.where("bvid").is(bvid));
        String msg = CreditConstant.REFRESH_VIDEO_DATA.getMsg(bvid);
        return refreshVideo(q, msg);
    }

    @Transactional(rollbackFor = Exception.class)
    private Result<?> refreshVideo(Query q, String msg) {
        Result<?> result = creditService.doCreditOperation(CreditConstant.REFRESH_VIDEO_DATA, msg, false);
        if (result.getCode() == -1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return result;
        }
        UserRecord ur = result.getUserRecord();
        mongoTemplate.updateFirst(q, new Update().addToSet("order", ur.getId()).set("next", new Date(0)), VideoIntervalRecord.class);
        result.setData(null);
        return result;
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
        Query q = new Query().addCriteria(Criteria.where(DbFields.BAN).ne(true)).with(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "exp")));
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
        return null;
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
        User user = userUtils.getUser();
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
        User user = userUtils.getUser();
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
        User user = userUtils.getUser();
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
    public ArrayList<?> getUserPreferVideoByFavoriteVideo(Integer page, Integer pagesize) {
        return recommendVideo.getRecommendVideoByTagCountMap(
                this.getUserPreferKeyword(), page, pagesize);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> bindMail(String mail, String activationCode) {
        User user = userUtils.getUser();
        if (user == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        if (!mailUtil.checkActivationCode(mail, activationCode)) {
            return new Result<>(ResultEnum.ACTIVATION_CODE_UNMATCHED);
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(mail)), User.class)) {
            return new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED);
        }
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(user.getId())),
                Update.update("mail", mail),
                User.class);
        return creditService.doCreditOperation(CreditConstant.MODIFY_MAIL, CreditConstant.MODIFY_MAIL.getMsg(mail));
    }

    /**
     * modify user's name
     *
     * @param newUserName new user name
     * @return operation result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> modifyUserName(@Valid String newUserName) {
        User user = userUtils.getUser();
        if (user == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        if (newUserName.length() > 20) {
            return new Result<>(ResultEnum.OUT_OF_RANGE);
        }
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(user.getId())),
                Update.update("nickName", newUserName),
                "user");
        return creditService.doCreditOperation(CreditConstant.MODIFY_NAME, CreditConstant.MODIFY_NAME.getMsg(newUserName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> changeNickName(String newNickname) {
        return this.modifyUserName(newNickname);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> changeMail(String newMail) {
        User user = userUtils.getUser();
        if (user == null) {
            return new Result<>(ResultEnum.HAS_NOT_LOGGED_IN);
        }
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(newMail)), User.class)) {
            return new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED);
        }
        mongoTemplate.update(User.class).matching(Criteria.where(DbFields.ID).is(user.getId())).apply(Update.update("mail", newMail)).first();
        return creditService.doCreditOperation(CreditConstant.MODIFY_MAIL, CreditConstant.MODIFY_MAIL.getMsg(newMail));
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
        User user = userUtils.getUser();
        if (user != null) {
            String userName = user.getName();
            return mongoTemplate.find(Query.query(Criteria.where("userName").is(userName)).with(Sort.by("_id").descending()).limit(100), UserRecord.class);
        } else {
            return null;
        }
    }

    @Override
    public void setVersion(String ver) {
        Date d = Calendar.getInstance().getTime();
        VersionRecord vr = new VersionRecord();
        vr.setDate(d);
        vr.setVersion(ver);
        mongoTemplate.save(vr);
    }
}
