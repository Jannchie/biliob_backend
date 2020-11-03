package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.FansGuessingItem;
import com.jannchie.biliob.model.GuessingItem;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.object.UserGuessingResult;
import com.jannchie.biliob.utils.BiliobUtils;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jannchie.biliob.constant.TimeConstant.MICROSECOND_OF_MINUTES;

/**
 * @author Jannchie
 */
@Service
@EnableTransactionManagement
public class GuessingService {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    ApplicationContext appContext;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BiliobUtils biliobUtils;
    @Autowired
    private CreditService creditService;

    public Date getCorrectGuessingTime(FansGuessingItem.PokerChip pokerChip) {
        Calendar.getInstance().getTime();
        Calendar guessingCal = Calendar.getInstance();
        guessingCal.setTime(pokerChip.getGuessingDate());
        guessingCal.add(Calendar.HOUR, -8);
        return guessingCal.getTime();
    }

    public List<FansGuessingItem> listFansGuessing(Integer page) {
        Calendar tempC = Calendar.getInstance();
        tempC.add(Calendar.DATE, -7);
        Criteria criteria = new Criteria().orOperator(Criteria.where("reachDate").is(null), Criteria.where("reachDate").gt(tempC.getTime()));
        Query q = new Query(criteria).with(PageRequest.of(page, 100, Sort.by("state").descending()));
        List<FansGuessingItem> result = mongoTemplate.find(q, FansGuessingItem.class);

        Set<ObjectId> userIdSet = new HashSet<>();
        result.forEach(fgi -> {
            if (fgi == null || fgi.getPokerChips() == null) {
                return;
            }
            fgi.getPokerChips().forEach(pokerChip -> {
                ObjectId id = pokerChip.getUser().getId();
                userIdSet.add(id);
            });
            userIdSet.add(fgi.getCreator().getId());
        });
        Map<String, String> nameToNickNameMap = biliobUtils.getNameToNickNameMap(userIdSet);

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
        Map<String, Long> tempMap = new HashMap<>(30);
        result.forEach(fansGuessingItem -> {
            String title = fansGuessingItem.getTitle().substring(0, 5);
            if (!tempMap.containsKey(title) || tempMap.get(title) > fansGuessingItem.getTarget()) {
                tempMap.put(fansGuessingItem.getTitle().substring(0, 5), fansGuessingItem.getTarget());
            }
        });
        result = result.stream().filter(fansGuessingItem -> tempMap.get(fansGuessingItem.getTitle().substring(0, 5)).equals(fansGuessingItem.getTarget())).collect(Collectors.toList());


        result.forEach(fgi -> {
            if (fgi == null || fgi.getResult() == null) {
                return;
            }
            fgi.getResult().forEach(userGuessingResult -> {
                userGuessingResult.setName(nameToNickNameMap.get(userGuessingResult.getName()));
                userGuessingResult.setRevenue(BigDecimal.valueOf(userGuessingResult.getRevenue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            });
            User u = new User();
            u.setName(fgi.getCreator().getName());
            u.setNickName(nameToNickNameMap.get(fgi.getCreator().getName()));
            fgi.setCreator(u);
        });
        return result;
    }

    public Result<?> announceResult(ObjectId guessingId, Integer index) {
        return null;
    }

    @Scheduled(initialDelay = MICROSECOND_OF_MINUTES * 10, fixedDelay = MICROSECOND_OF_MINUTES * 10)
    @Async
    @Transactional(rollbackFor = Exception.class)
    public Result<?> autoUpdateGuessing() {
        logger.info("查看竞猜是快要达成");
        Query q = new Query(Criteria.where("state").is(1));
        List<FansGuessingItem> fansGuessingItems = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("state").lt(3)),
                        Aggregation.lookup("author", "author.mid", "mid", "author"),
                        Aggregation.unwind("author"),
                        Aggregation.project().andExpression("{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}").as("author").and("target")
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
            } else if (fansGuessingItem.getAuthor().getcFans() + 100000 > fansGuessingItem.getTarget()) {
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

    @Scheduled(initialDelay = MICROSECOND_OF_MINUTES * 10, fixedDelay = MICROSECOND_OF_MINUTES * 60)
    @Async
    public Result<?> autoPostAuthorFansGuessing() {
        logger.info("添加预测竞猜");
        long minFans = 5000000L;
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

        while (nextTop >= minFans) {
            logger.info("寻找预测竞猜：粉丝数突破 {}", nextTop);
            q = new Query(Criteria.where("cFans").lt(nextTop - 100000).gt(nextTop - 1000000).and("cRate").gt(0)).with(Sort.by("cFans").descending());
            q.fields().include("mid").include("name");
            Author nextAuthor = mongoTemplate.findOne(q, Author.class);
            if (nextAuthor == null) {
                nextTop -= 1000000;
                continue;
            }
            if (!mongoTemplate.exists(Query.query(Criteria.where("author.mid").is(nextAuthor.getMid()).and("target").is(nextTop)), FansGuessingItem.class)) {
                FansGuessingItem fansGuessingItem = new FansGuessingItem();
                fansGuessingItem.setAuthor(nextAuthor);
                fansGuessingItem.setTarget(nextTop);
                String title = String.format("%s粉丝数突破%,d的时间预测", nextAuthor.getName(), nextTop);
                fansGuessingItem.setTitle(title);
                logger.info("添加预测竞猜[{}]", title);
                fansGuessingItem.setCreator(user);
                fansGuessingItem.setState(1);
                mongoTemplate.save(fansGuessingItem);
            }
            nextTop -= 1000000;
        }
        return new Result<>(ResultEnum.SUCCEED);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<?> joinFansGuessing(String guessingId, FansGuessingItem.PokerChip pokerChip) {
        Calendar guessingCal = Calendar.getInstance();
        guessingCal.setTime(pokerChip.getGuessingDate());
        Calendar calendar = Calendar.getInstance();
        Date correctGuessingTime = getCorrectGuessingTime(pokerChip);
        if (correctGuessingTime.before(calendar.getTime())) {
            return new Result<>(ResultEnum.EXECUTE_FAILURE);
        }
        if (pokerChip.getCredit() <= 0 || pokerChip.getCredit() > 100) {
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
        User userInfo = new User();
        if (user == null) {
            return ResultEnum.HAS_NOT_LOGGED_IN.getResult();
        }
        double sum = fansGuessingItem.getPokerChips().stream().filter((p) -> p.getUser().getName().equals(user.getName())).mapToDouble(GuessingItem.PokerChip::getCredit).sum();
        if (sum >= 500) {
            return ResultEnum.EXCEED_PREDICT_LIMIT.getResult();
        }
        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        pokerChip.setCreateTime(Calendar.getInstance().getTime());
        pokerChip.setUser(userInfo);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("guessingId").is(guessingId)),
                new Update().push("pokerChips", pokerChip),
                FansGuessingItem.class);
        return creditService.doCreditOperationFansGuessing(user, CreditConstant.JOIN_GUESSING, CreditConstant.JOIN_GUESSING.getMsg(guessingId), -pokerChip.getCredit());
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(initialDelay = 0, fixedDelay = MICROSECOND_OF_MINUTES * 60 * 24)
    public Result<?> judgeFinishedFansGuessing() {
        logger.info("计算收益");
        try {
            Integer finishedState = 3;
            List<FansGuessingItem> fansGuessingItems = mongoTemplate.find(Query.query(Criteria.where("state").is(finishedState)), FansGuessingItem.class);
            for (FansGuessingItem f : fansGuessingItems
            ) {
                ArrayList<UserGuessingResult> resultList = getUserGuessingResults(f);
                mongoTemplate.updateFirst(Query.query(Criteria.where("guessingId").is(f.getGuessingId())), Update.update("result", resultList).set("state", 4), FansGuessingItem.class);
                resultList.forEach(userGuessingResult -> {
                    User u = mongoTemplate.findOne(Query.query(Criteria.where("name").is(userGuessingResult.getName())), User.class);
                    if (u == null) {
                        return;
                    }
                    if (!mongoTemplate.exists(Query.query(Criteria.where("name").is(userGuessingResult.getName()).and("guessingId").is(f.getGuessingId())), UserGuessingResult.class, "cashed_user_guessing")) {
                        userGuessingResult.setGuessingId(f.getGuessingId());
                        creditService.doCreditOperationFansGuessing(u, CreditConstant.GUESSING_REVENUE, CreditConstant.GUESSING_REVENUE.getMsg(f.getGuessingId()), userGuessingResult.getRevenue());
                        mongoTemplate.save(userGuessingResult, "cashed_user_guessing");
                    }
                });
            }
            return new Result<>(ResultEnum.SUCCEED);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    private ArrayList<UserGuessingResult> getUserGuessingResults(FansGuessingItem f) {
        Date finalReachDate = getFinalReachDate(f);
        return getUserGuessingResults(f, finalReachDate);
    }

    private ArrayList<UserGuessingResult> getUserGuessingResults(FansGuessingItem f, Date finalReachDate) {
        List<GuessingItem.PokerChip> pokerChipList = f.getPokerChips();
        ArrayList<UserGuessingResult> results = new ArrayList<>();
        Double rate = 0.47;
        if (pokerChipList != null) {
            for (int i = 0; i < pokerChipList.size(); i++) {
                GuessingItem.PokerChip p = pokerChipList.get(i);
                int range = 10;
                int l = Math.max(i - range, 0);
                int r = Math.min(i + range, pokerChipList.size());

                long averageLossHour = 0L;
                for (int j = l; j < r; j++) {
                    GuessingItem.PokerChip t = pokerChipList.get(j);
                    Date guessingDate = getCorrectGuessingTime(t);
                    Long lossHour = Math.abs((finalReachDate.getTime() - guessingDate.getTime()) / 3600000);
                    averageLossHour += lossHour;
                }
                averageLossHour /= (l - r);

                String userName = p.getUser().getName();
                Date guessingDate = getCorrectGuessingTime(p);
                Double credit = p.getCredit();
                Date createTime = p.getCreateTime();

                Long lossHour = Math.abs((finalReachDate.getTime() - guessingDate.getTime()) / 3600000);
                Long foreHour = (finalReachDate.getTime() - createTime.getTime()) / 3600000;

                UserGuessingResult result;
                result = new UserGuessingResult();
                result.setForeHour(foreHour);
                result.setLossHour(lossHour);
                Long score = getScore(lossHour, foreHour, averageLossHour);
                result.setScore((long) (score * rate * credit));
                result.setCredit(credit);
                result.setAverageDate(guessingDate);
                result.setAverageCreateTime(createTime);
                result.setName(userName);
                results.add(result);
            }
        }

        Long sumScore = results.stream().map(UserGuessingResult::getScore).reduce(0L, Long::sum);
        Double sumCredit = results.stream().map(UserGuessingResult::getCredit).reduce(0D, Double::sum);
        Double useCredit = sumCredit * rate;
        Double scoreCredit = useCredit / sumScore;
        results.forEach(r -> {
            double revenue = r.getCredit() * (1 - rate) + r.getScore() * scoreCredit;
            r.setRevenue(new BigDecimal(revenue).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        });
        Map<String, UserGuessingResult> r = new HashMap<>(100);
        Map<String, Integer> c = new HashMap<>(100);
        Map<String, Long> avgC = new HashMap<>(100);
        Map<String, Long> avgF = new HashMap<>(100);
        Map<String, Long> avgP = new HashMap<>(100);
        Map<String, Long> avgD = new HashMap<>(100);
        for (UserGuessingResult res : results
        ) {
            if (r.containsKey(res.getName())) {
                UserGuessingResult tmp = r.get(res.getName());
                tmp.setScore(tmp.getScore() + res.getScore());
                tmp.setRevenue(tmp.getRevenue() + res.getRevenue());
                tmp.setCredit(tmp.getCredit() + res.getCredit());
                avgC.put(res.getName(), avgC.get(res.getName()) + res.getAverageCreateTime().getTime());
                avgF.put(res.getName(), avgF.get(res.getName()) + res.getForeHour());
                avgP.put(res.getName(), avgP.get(res.getName()) + res.getLossHour());
                avgD.put(res.getName(), avgD.get(res.getName()) + res.getAverageDate().getTime());
                c.put(tmp.getName(), c.get(tmp.getName()) + 1);
            } else {
                avgC.put(res.getName(), res.getAverageCreateTime().getTime());
                avgD.put(res.getName(), res.getAverageDate().getTime());
                avgF.put(res.getName(), res.getForeHour());
                avgP.put(res.getName(), res.getLossHour());
                r.put(res.getName(), res);
                c.put(res.getName(), 1);
            }
        }
        r.values().forEach((res) -> {
            res.setAverageCreateTime(new Date(avgC.get(res.getName()) / c.get(res.getName())));
            res.setAverageDate(new Date(avgD.get(res.getName()) / c.get(res.getName())));
            res.setForeHour(avgF.get(res.getName()) / c.get(res.getName()));
            res.setLossHour(avgP.get(res.getName()) / c.get(res.getName()));
            res.setRevenue(BigDecimal.valueOf(res.getRevenue()).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        });
        results = new ArrayList<>(r.values());
        return results;
    }


    private Long getScore(Long lossHour, Long foreHour, long averageLossHour) {
        double error = lossHour.doubleValue() / foreHour.doubleValue();
        if (error > 1) {
            error = 1;
        }
        long lossBetter = averageLossHour - lossHour;
        if (lossBetter > 147) {
            lossBetter = 147;
        }
        if (lossBetter < -47) {
            lossBetter = -47;
        }
        long score = 147;
        score += lossBetter;
        score += foreHour / 720;
        score -= 100 * error;
        if (lossHour < 24 && foreHour > 30 * 24 || lossHour < 24 * 7 && foreHour > 90 * 24 || lossHour < 24 * 14 && foreHour > 120 * 24) {
            score *= 4;
        } else if (lossHour < 48 && foreHour > 30 * 24 || lossHour < 24 * 14 && foreHour > 90 * 24 || lossHour < 24 * 21 && foreHour > 120 * 24) {
            score *= 3;
        } else if (lossHour < 24 * 3 && foreHour > 14 * 24 || lossHour < 24 * 21 && foreHour > 90 * 24 || lossHour < 24 * 28 && foreHour > 120 * 24) {
            score *= 2;
        } else if (lossHour < 3 && foreHour > 7 * 24) {
            score *= 1.8;
        } else if (lossHour < 6 && foreHour > 5 * 24) {
            score *= 1.5;
        } else if (lossHour < 12) {
            score *= 1.2;
        }
        return score;
    }

    private Date getFinalReachDate(FansGuessingItem f) {
        Date reachDate = f.getReachDate();
        Calendar c = Calendar.getInstance();
        c.setTime(reachDate);
        c.add(Calendar.HOUR, -8);
        return c.getTime();
    }

    public void printGuessingResult(String guessingId) {
        FansGuessingItem fansGuessingItem = mongoTemplate.findOne(Query.query(Criteria.where("guessingId").is(guessingId)), FansGuessingItem.class);
        assert fansGuessingItem != null;
        printGuessingResult(guessingId, getFinalReachDate(fansGuessingItem));

    }

    public void printGuessingResult(String guessingId, Date finalReachDate) {
        FansGuessingItem fansGuessingItem = mongoTemplate.findOne(Query.query(Criteria.where("guessingId").is(guessingId)), FansGuessingItem.class);
        assert fansGuessingItem != null;
        ArrayList<UserGuessingResult> resultList = getUserGuessingResults(fansGuessingItem, finalReachDate);
        resultList.sort((a, b) -> {
            double x = (b.getRevenue() / b.getCredit());
            double y = (a.getRevenue() / a.getCredit());
            return Double.compare(x, y);
        });

        resultList.forEach(r -> System.out.printf("%s 收益为 %f，提前%d预测，偏差%d, 收益率为%.2f\n", r.getName(), r.getRevenue(), r.getForeHour() / 24, r.getLossHour() / 24, r.getRevenue() / r.getCredit()));
        double sumR = 0;
        double sumC = 0;
        int winCount = 0;
        for (UserGuessingResult r : resultList) {
            sumR += r.getRevenue();
            sumC += r.getCredit();
            if (r.getRevenue() > r.getCredit()) {
                winCount++;
            }
        }
        System.out.printf("合计收益: %f\n合计积分: %f\n赚率：%d\n", sumR, sumC, winCount * 100 / resultList.size());
    }

    public Result<?> getGuessingResult(String guessingId) {
        FansGuessingItem fansGuessingItem = mongoTemplate.findOne(Query.query(Criteria.where("guessingId").is(guessingId)), FansGuessingItem.class);
        if (fansGuessingItem == null) {
            return null;
        }
        ArrayList<UserGuessingResult> resultList = getUserGuessingResults(fansGuessingItem);
        return new Result<>(ResultEnum.SUCCEED, resultList);
    }

    public Result<?> cancelRevenue(String guessingId) {
        List<UserGuessingResult> ugrs = mongoTemplate.find(Query.query(Criteria.where("guessingId").is(guessingId)), UserGuessingResult.class, "cashed_user_guessing");
        for (UserGuessingResult ugr : ugrs
        ) {
            String userName = ugr.getName();
            Double revenue = ugr.getRevenue();
            mongoTemplate.upsert(Query.query(Criteria.where("name").is(userName)), new Update().inc("credit", -revenue), User.class);
        }
        return new Result<>(ResultEnum.SUCCEED);
    }
}
