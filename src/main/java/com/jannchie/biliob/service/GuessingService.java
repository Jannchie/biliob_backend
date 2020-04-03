package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.FansGuessingItem;
import com.jannchie.biliob.model.GuessingItem;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.object.UserGuessingResult;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jannchie.biliob.constant.TimeConstant.MICROSECOND_OF_MINUTES;

/**
 * @author Jannchie
 */
@Service
public class GuessingService {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CreditOperateHandle creditOperateHandle;

    public Date getCorrectGuessingTime(FansGuessingItem.PokerChip pokerChip) {
        Calendar.getInstance().getTime();
        Calendar guessingCal = Calendar.getInstance();
        guessingCal.setTime(pokerChip.getGuessingDate());
        guessingCal.add(Calendar.HOUR, -8);
        return guessingCal.getTime();
    }

    public List<FansGuessingItem> listFansGuessing(Integer page) {
        Query q = new Query().with(PageRequest.of(page, 10, Sort.by("date").ascending()));
        List<FansGuessingItem> result = mongoTemplate.find(q, FansGuessingItem.class);

        result.forEach(fansGuessingItem -> {
            Double totalCredit = 0D;
            if (fansGuessingItem.getPokerChips() != null) {
                for (GuessingItem.PokerChip pokerChip : fansGuessingItem.getPokerChips()
                ) {
                    totalCredit += pokerChip.getCredit();
                }
            }

            fansGuessingItem.setTotalCredit(totalCredit);

            fansGuessingItem.setTotalUser(fansGuessingItem.getPokerChips() == null ? 0 : fansGuessingItem.getPokerChips().size());

            long totalTime = 0L;
            double totalCredit2 = 0D;
            if (fansGuessingItem.getPokerChips() != null) {
                for (FansGuessingItem.PokerChip pokerChip : fansGuessingItem.getPokerChips()
                ) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, 1);
                    if (pokerChip.getGuessingDate().before(calendar.getTime())) {
                        totalTime += pokerChip.getGuessingDate().getTime() * pokerChip.getCredit();
                        totalCredit2 += pokerChip.getCredit();
                    }
                }
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis((long) (totalTime / totalCredit2));

                fansGuessingItem.setAverageTime(c.getTime());
            }
            fansGuessingItem.setPokerChips(null);
        });
        return result;
    }

    public Result<?> joinGuessing(ObjectId guessingId, Integer index, Double value) {
        return null;
    }

    public Result<?> announceResult(ObjectId guessingId, Integer index) {
        return null;
    }

    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES)
    @Async
    public Result<?> autoUpdateGuessing() {
        logger.info("查看竞猜是快要达成");
        Query q = new Query(Criteria.where("state").is(1));
        List<FansGuessingItem> fansGuessingItems = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("state").ne(3)),
                        Aggregation.lookup("author", "author.mid", "mid", "author"),
                        Aggregation.unwind("author"),
                        Aggregation.project().andExpression("{ data: 0, keyword: 0}").as("author").and("target")
                ), FansGuessingItem.class, FansGuessingItem.class
        ).getMappedResults();
        fansGuessingItems.forEach(fansGuessingItem -> {
            if (fansGuessingItem.getAuthor().getcFans() > fansGuessingItem.getTarget()) {
                logger.info("竞猜[{}]已经达成", fansGuessingItem.getTitle());
                Query query = Query.query(Criteria.where("mid").is(fansGuessingItem.getAuthor().getMid()).and("fans").gt(fansGuessingItem.getTarget())).with(Sort.by("fans").ascending());
                query.fields().include("mid").include("datetime");
                Author.Data data = mongoTemplate.findOne(query, Author.Data.class);
                assert data != null;
                mongoTemplate.updateFirst(
                        Query.query(Criteria.where("guessingId").is(fansGuessingItem.getGuessingId())),
                        Update.update("state", 3).set("reachDate", data.getDatetime()),
                        FansGuessingItem.class);
            } else if (fansGuessingItem.getAuthor().getcFans() + 20000 > fansGuessingItem.getTarget()) {
                logger.info("竞猜[{}]已经快要达成", fansGuessingItem.getTitle());
                mongoTemplate.updateFirst(
                        Query.query(Criteria.where("guessingId").is(fansGuessingItem.getGuessingId())),
                        Update.update("state", 2),
                        FansGuessingItem.class);
            } else {
                Query query = Query.query(Criteria.where("mid").is(fansGuessingItem.getAuthor().getMid())).with(Sort.by("fans").descending());
                query.fields().include("mid").include("datetime").include("fans");
                Author.Data data = mongoTemplate.findOne(query, Author.Data.class);
                assert data != null;
                logger.info("{} 还差 {}", fansGuessingItem.getTitle(), fansGuessingItem.getTarget() - data.getFans());
            }
        });
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES * 10)
    @Async
    public Result<?> autoPostAuthorFansGuessing() {
        logger.info("添加预测竞猜");
        Query q = new Query().with(Sort.by("cFans").descending());
        q.fields().include("mid").include("cFans");
        Author topAuthorData = mongoTemplate.findOne(q, Author.class);
        assert topAuthorData != null;
        Long topFans = Long.valueOf(topAuthorData.getcFans());
        // 下一个整百万：
        long nextTop = topFans + 1000000 - topFans % 1000000;
        // 大于500万
        Query userQuery = Query.query(Criteria.where("name").is("jannchie"));
        userQuery.fields().include("name");
        User user = mongoTemplate.findOne(userQuery, User.class);
        while (nextTop > 5000000) {
            q = new Query(Criteria.where("cFans").lt(nextTop).and("cRate").gt(0)).with(Sort.by("cFans").descending());
            q.fields().include("mid").include("name");
            Author nextAuthor = mongoTemplate.findOne(q, Author.class);
            assert nextAuthor != null;
            if (mongoTemplate.exists(Query.query(Criteria.where("author.mid").is(nextAuthor.getMid()).and("target").is(nextTop)), FansGuessingItem.class)) {
                continue;
            }
            FansGuessingItem fansGuessingItem = new FansGuessingItem();
            fansGuessingItem.setAuthor(nextAuthor);
            fansGuessingItem.setTarget(nextTop);
            String title = String.format("%s粉丝数突破%,d的时间预测", nextAuthor.getName(), nextTop);
            fansGuessingItem.setTitle(title);
            logger.info("添加预测竞猜[{}]", title);
            fansGuessingItem.setCreator(user);
            fansGuessingItem.setState(1);
            mongoTemplate.save(fansGuessingItem);
            nextTop -= 1000000;
        }
        return new Result<>(ResultEnum.SUCCEED);
    }

    public Result<?> joinFansGuessing(String guessingId, FansGuessingItem.PokerChip pokerChip) {
        Calendar guessingCal = Calendar.getInstance();
        guessingCal.setTime(pokerChip.getGuessingDate());
        Calendar calendar = Calendar.getInstance();
        Date correctGuessingTime = getCorrectGuessingTime(pokerChip);
        if (correctGuessingTime.before(calendar.getTime())) {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
        calendar.add(Calendar.YEAR, 1);
        if (correctGuessingTime.after(calendar.getTime())) {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
        Query q = Query.query(Criteria.where("guessingId").is(guessingId));
        q.fields().include("state");
        FansGuessingItem fansGuessingItem = mongoTemplate.findOne(Query.query(Criteria.where("guessingId").is(guessingId)), FansGuessingItem.class);
        if (fansGuessingItem == null) {
            return new Result<>(ResultEnum.NOT_FOUND);
        }
        if (fansGuessingItem.getState() != 1) {
            return new Result<>(ResultEnum.ALREADY_FINISHED);
        }
        User user = UserUtils.getUser();
        return creditOperateHandle.doCustomCreditOperate(user, pokerChip.getCredit(), CreditConstant.JOIN_GUESSING, () -> {
            User userInfo = new User();
            userInfo.setId(user.getId());
            userInfo.setName(user.getName());
            pokerChip.setCreateTime(Calendar.getInstance().getTime());
            pokerChip.setUser(userInfo);
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("guessingId").is(guessingId)),
                    new Update().push("pokerChips", pokerChip),
                    FansGuessingItem.class);
            return null;
        });
    }

    @Scheduled(fixedDelay = MICROSECOND_OF_MINUTES * 10)
    @Async
    public Result<?> judgeFinishedFansGuessing() {
        Integer finishedState = 3;
        List<FansGuessingItem> fansGuessingItems = mongoTemplate.find(Query.query(Criteria.where("state").is(finishedState)), FansGuessingItem.class);
        for (FansGuessingItem f : fansGuessingItems
        ) {
            Date reachDate = f.getReachDate();
            Calendar c = Calendar.getInstance();
            c.setTime(reachDate);
            c.add(Calendar.HOUR, -8);

            Date finalReachDate = c.getTime();
            HashMap<String, Long> result = new HashMap<>();
            HashMap<String, Double> sumCreditMap = new HashMap<>();
            HashMap<String, Long> sumTimeStamp = new HashMap<>();
            HashMap<String, Long> sumCreateTimeStamp = new HashMap<>();
            f.getPokerChips().forEach(pokerChip -> {

                Date cDate = pokerChip.getCreateTime();
                User user = pokerChip.getUser();
                String name = user.getName();
                Date guessingDate = getCorrectGuessingTime(pokerChip);
                long deltaTime = Math.abs(guessingDate.getTime() - finalReachDate.getTime());
                if (deltaTime == 0) {
                    deltaTime = 1L;
                }
                Double credit = pokerChip.getCredit();
                // 积分数 = 筹码积分值 × ( 实际达成时间 - 发起预测时间 ) ÷ ( | 实际达成时间 - 预测达成时间 |)
                Long score = credit.longValue() * ((finalReachDate.getTime() - cDate.getTime()) / deltaTime);

                if (sumCreditMap.containsKey(name)) {
                    sumCreditMap.put(name, sumCreditMap.get(name) + credit);
                } else {
                    sumCreditMap.put(name, credit);
                }

                if (sumTimeStamp.containsKey(name)) {
                    sumTimeStamp.put(name, sumTimeStamp.get(name) + guessingDate.getTime() * credit.longValue());
                } else {
                    sumTimeStamp.put(name, guessingDate.getTime() * credit.longValue());
                }
                if (sumCreateTimeStamp.containsKey(name)) {
                    sumCreateTimeStamp.put(name, sumCreateTimeStamp.get(name) + cDate.getTime() * credit.longValue());
                } else {
                    sumCreateTimeStamp.put(name, cDate.getTime() * credit.longValue());
                }

                if (result.containsKey(name)) {
                    result.put(name, result.get(name) + score);
                } else {
                    result.put(name, score);
                }
                logger.info("{} {} {} {}", name, result.get(name), guessingDate.toString(), cDate.toString());
            });
            logger.info(result);
            ArrayList<UserGuessingResult> resultList = new ArrayList<>();
            Calendar tempCal = Calendar.getInstance();
            long sumScore = result.values().stream().reduce(0L, Long::sum);
            long sumCredit = sumCreditMap.values().stream().reduce(0D, Double::sum).longValue();
            result.keySet().forEach(key -> {
                UserGuessingResult r = new UserGuessingResult();
                r.setScore(result.get(key));
                r.setName(key);
                r.setCredit(sumCreditMap.get(key));
                long averageGuessingTime = sumTimeStamp.get(key) / sumCreditMap.get(key).longValue();
                tempCal.setTimeInMillis(averageGuessingTime);
                r.setAverageDate(tempCal.getTime());
                long averageCreateTime = sumCreateTimeStamp.get(key) / sumCreditMap.get(key).longValue();
                tempCal.setTimeInMillis(averageCreateTime);
                r.setAverageCreateTime(tempCal.getTime());
                resultList.add(r);
            });
            resultList.sort(Comparator.comparingLong(UserGuessingResult::getScore).reversed());
            mongoTemplate.updateFirst(Query.query(Criteria.where("guessingId").is(f.getGuessingId())), Update.update("result", resultList), FansGuessingItem.class);
        }
        return new Result<>(ResultEnum.SUCCEED);
    }
}
