package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.FansGuessingItem;
import com.jannchie.biliob.model.User;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        return mongoTemplate.find(new Query().with(PageRequest.of(page, 10, Sort.by("date").ascending())), FansGuessingItem.class);
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
                        Aggregation.match(Criteria.where("state").is(1)),
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
                        Update.update("state", 2).set("reachDate", data.getDatetime()),
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


}
