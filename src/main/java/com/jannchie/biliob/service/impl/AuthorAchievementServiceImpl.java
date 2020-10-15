package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.AuthorAchievementEnum;
import com.jannchie.biliob.constant.AuthorUniqueAchievementEnum;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.AuthorDailyTrend;
import com.jannchie.biliob.service.AuthorAchievementService;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Jannchie
 */
@Service
public class AuthorAchievementServiceImpl implements AuthorAchievementService {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();
    private MongoTemplate mongoTemplate;
    private AuthorService authorService;

    @Autowired
    public AuthorAchievementServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * analyze author achievement
     *
     * @param mid author id
     * @return result
     */
    @Override
    @Async
    public Result<?> analyzeAuthorAchievement(Long mid) {
        logger.info("为 {} 计算唯一成就", mid);
        //
        List<Author.Achievement> achievements = mongoTemplate.find(Query.query(Criteria.where("author.mid").is(mid)).with(Sort.by("date").ascending()), Author.Achievement.class);
        HashSet<Integer> hashSet = new HashSet<>();
        achievements.forEach(a -> {
            hashSet.add(a.getCode());
        });
        Date lastDate = null;
        if (achievements.size() != 0) {
            lastDate = achievements.get(achievements.size() - 1).getDate();
        }
        Query q;
        if (lastDate == null) {
            q = Query.query(Criteria.where("mid").is(mid)).with(Sort.by("datetime").ascending());
        } else {
            q = Query.query(Criteria.where("mid").is(mid).and("datetime").gte(lastDate)).with(Sort.by("datetime").ascending());
        }
        List<Author.Data> dataList = mongoTemplate.find(q, Author.Data.class);
        doAddAchievements(mid, hashSet, dataList);
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Override
    @Async
    public void rapidlyAnalyzeAuthorAchievement(Author author) {
        if (author == null) {
            return;
        }
        List<Author.Achievement> achievements = author.getAchievements();
        HashSet<Integer> hashSet = new HashSet<>();
        achievements.forEach(a -> {
            hashSet.add(a.getCode());
        });
        List<Author.Data> dataList = author.getData();
        doRapidlyAddAchievements(author.getMid(), hashSet, dataList);

        new Result<>(ResultEnum.SUCCEED);
    }

    private void doRapidlyAddAchievements(Long mid, HashSet<Integer> hashSet, List<Author.Data> dataList) {
        for (AuthorUniqueAchievementEnum e : AuthorUniqueAchievementEnum.values()
        ) {
            int size = dataList.size();
            if (size < 1) {
                return;
            }
            //已经获得该成就，则跳过
            if (hashSet.contains(e.getId())) {
                continue;
            }
            Long lastFans = dataList.get(0).getFans();
            Long lastLike = dataList.get(0).getLike();
            Long lastView = dataList.get(0).getArchiveView();
            for (Author.Data value : dataList) {
                if (lastFans == null) {
                    lastFans = value.getFans();
                }
                if (lastLike == null) {
                    lastLike = value.getLike();
                }
                if (lastView == null) {
                    lastView = value.getArchiveView();
                }
                if (lastFans != null && lastView != null && lastLike != null) {
                    break;
                }
            }

            Long initFans = dataList.get(size - 1).getFans();
            Long initLike = dataList.get(size - 1).getLike();
            Long initView = dataList.get(size - 1).getArchiveView();
            for (int i = size - 1; i >= 0; i--) {
                if (initFans == null) {
                    initFans = dataList.get(i).getFans();
                }
                if (initLike == null) {
                    initLike = dataList.get(i).getLike();
                }
                if (initView == null) {
                    initView = dataList.get(i).getArchiveView();
                }
                if (initView != null && initLike != null && initFans != null) {
                    break;
                }
            }


            Long val = e.getValue();
            String key;
            if (lastFans == null || lastView == null || lastLike == null) {
                return;
            }
            Long initData;
            if (e.getId() < 3010 && lastFans > e.getValue()) {
                key = "fans";
                initData = initFans;
            } else if (e.getId() >= 3010 && e.getId() < 3020 && lastView > e.getValue()) {
                key = "archiveView";
                initData = initView;
            } else if (e.getId() >= 3020 && e.getId() < 3030 && lastLike > e.getValue()) {
                key = "like";
                initData = initLike;
            } else {
                continue;
            }
            if (initData == null) {
                continue;
            }
            Author.Data data = mongoTemplate.findOne(
                    Query.query(
                            Criteria.where("mid").is(mid).and(key).gt(val))
                            .with(Sort.by("datetime").ascending()), Author.Data.class);
            if (data == null) {
                continue;
            }
            Author.Achievement a;
            if ("fans".equals(key)) {
                a = new Author.Achievement(e, mid, data.getFans());
            } else if ("archiveView".equals(key)) {
                a = new Author.Achievement(e, mid, data.getArchiveView());
            } else {
                a = new Author.Achievement(e, mid, data.getLike());
            }
            // 如果初始值大于判定点，则不填充date
            if (initData > e.getValue()) {
                a.setDate(null);
            } else {
                a.setDate(data.getDatetime());
            }
            mongoTemplate.save(a);
            logger.info("为 {} 添加成就 {}", mid, a.getName());
        }
    }

    private void doAddAchievements(Long mid, HashSet<Integer> hashSet, List<Author.Data> dataList) {
        for (AuthorAchievementEnum eachAchieve : AuthorAchievementEnum.values()) {
            Integer id = eachAchieve.getId();
            if (id >= 3000 && id < 3100) {
                if (hashSet.contains(id)) {
                    continue;
                }
                checkValue(id, mid, dataList);
            }
        }
    }

    private void checkValue(Integer id, Long mid, List<Author.Data> dataList) {
        AuthorAchievementEnum ae = AuthorAchievementEnum.getById(id);

        long lastVal = 0L;
        for (int i = 1; i < dataList.size(); i++) {
            Author.Data data = dataList.get(i);
            Long val;
            if (id >= 3000 && id < 3010) {
                val = data.getFans();
            } else if (id >= 3010 && id < 3020) {
                val = data.getArchiveView();
            } else {
                val = data.getLike();
            }

            if (ae == null) {
                return;
            }
            if (val > ae.getValue()) {
                Author.Achievement a = new Author.Achievement(ae, mid);
                if (lastVal < ae.getValue()) {
                    a.setDate(data.getDatetime());
                } else {
                    a.setDate(null);
                }
                mongoTemplate.insert(a);
                logger.info("为 {} 添加成就 {}", mid, ae.getName());
                return;
            }
            lastVal = val;
        }
    }

    /**
     * analyze all author achievement
     *
     * @return result
     */
    @Override
    public Result<?> analyzeAllAuthorAchievement() {

        return null;
    }

    @Override
    @Async
    public void analyzeDailyAchievement(Long mid) {
        List<AuthorDailyTrend> dailyTrends = mongoTemplate.find(Query.query(Criteria.where("mid").is(mid)), AuthorDailyTrend.class);
        if (dailyTrends.size() > 1) {
            AuthorDailyTrend pData = dailyTrends.get(0);
            for (AuthorDailyTrend cData : dailyTrends) {
                if (cData.getFans() == null) {
                    continue;
                }
                AuthorAchievementEnum[] increaseInDayAchievements = {
                        AuthorAchievementEnum.INCREASE_IN_DAY_LV10,
                        AuthorAchievementEnum.INCREASE_IN_DAY_LV9,
                        AuthorAchievementEnum.INCREASE_IN_DAY_LV8};

                AuthorAchievementEnum[] decreaseInDayAchievements = {
                        AuthorAchievementEnum.REDUCE_IN_DAY_LV9,
                        AuthorAchievementEnum.REDUCE_IN_DAY_LV8,
                        AuthorAchievementEnum.REDUCE_IN_DAY_LV7};

                AuthorAchievementEnum[] increaseAchievements = {
                        AuthorAchievementEnum.INCREASE_LV5,
                        AuthorAchievementEnum.INCREASE_LV4,
                        AuthorAchievementEnum.INCREASE_LV3};

                for (AuthorAchievementEnum achievementEnum : increaseInDayAchievements) {
                    if (checkAndAddFansIncreaseAchievement(cData, achievementEnum)) {
                        break;
                    }
                }
                for (AuthorAchievementEnum achievementEnum : decreaseInDayAchievements) {

                    if (checkAndAddFansDecreaseAchievement(cData, achievementEnum)) {
                        break;
                    }
                }

                if (pData.getFans() > 100 && cData.getFans() < -500) {
                    AuthorAchievementEnum e = AuthorAchievementEnum.UP_TO_DOWN;
                    insertAchievementIfNotExist(cData, e, cData.getFans());
                }

                for (AuthorAchievementEnum achievementEnum : increaseAchievements) {
                    if (pData.getFans() < 0) {
                        continue;
                    }
                    if (cData.getFans() > pData.getFans() * achievementEnum.getValue()) {
                        Author.Data d = new Author.Data();
                        if (pData.getFans() == 0) {
                            continue;
                        }
                        d.setMid(cData.getMid());
                        d.setDatetime(cData.getDatetime());
                        insertAchievementIfNotExist(cData, achievementEnum, cData.getFans() / pData.getFans());
                        break;
                    }
                }
                pData = cData;
            }

            new Result<>(ResultEnum.SUCCEED);
        } else {
            new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
    }

    @Override
    @Cacheable(value = "achievement_by_level", key = "#level")
    public List<Author.Achievement> getAuthorAchievementByLevel(Integer level) {
        logger.info("查询Lv.{}以上的最新B站事件", level);
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.sort(Sort.by("date").descending()),
                        Aggregation.match(Criteria.where("level").gt(level)),
                        Aggregation.limit(20),
                        Aggregation.lookup("author", "author.mid", "mid", "author"),
                        Aggregation.unwind("author"),
                        Aggregation.project().andExpression("{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}").as("author")
                ), Author.Achievement.class, Author.Achievement.class
        ).getMappedResults();
    }

    private boolean checkAndAddFansDecreaseAchievement(AuthorDailyTrend data, AuthorAchievementEnum achievementEnum) {
        Long cFans = data.getFans();
        if (cFans < -achievementEnum.getValue()) {
            insertAchievementIfNotExist(data, achievementEnum, cFans);
            return true;
        }
        return false;
    }

    private boolean checkAndAddFansIncreaseAchievement(AuthorDailyTrend data, AuthorAchievementEnum achievementEnum) {
        Long cFans = data.getFans();
        if (cFans > achievementEnum.getValue()) {
            insertAchievementIfNotExist(data, achievementEnum, cFans);
            return true;
        }
        return false;
    }

    private void insertAchievementIfNotExist(AuthorDailyTrend data, AuthorAchievementEnum achievementEnum, Long value) {
        boolean wasGotten = mongoTemplate.exists(
                Query.query(
                        Criteria.where("author.mid").is(data.getMid())
                                .and("code").is(achievementEnum.getId())
                                .and("date").is(data.getDatetime())), Author.Achievement.class);
        if (!wasGotten) {
            logger.info("添加成就: {} {}", data.getMid(), achievementEnum.getName());
            mongoTemplate.save(new Author.Achievement(achievementEnum, data.getMid(), value, data.getDatetime()));
        }
    }
}
