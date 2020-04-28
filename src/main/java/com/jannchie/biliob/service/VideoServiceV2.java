package com.jannchie.biliob.service;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditOperateHandle;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.object.VideoIntervalRecord;
import com.jannchie.biliob.utils.Result;
import com.jannchie.biliob.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Jannchie
 */
@Service
public class VideoServiceV2 {
    private static Logger logger = LogManager.getLogger();
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CreditOperateHandle creditOperateHandle;

    public Video getVideoDetailByAid(Long aid) {
        Criteria c = Criteria.where("aid").is(aid);
        logger.info("获得AV{}的数据", aid);
        return getVideoWithAuthorDataByCriteria(c);
    }

    public Video getVideoDetailByBvid(String bvid) {
        Criteria c = Criteria.where("bvid").is(bvid);
        logger.info("获得BV{}的数据", bvid);
        return getVideoWithAuthorDataByCriteria(c);
    }

    public Result<?> addVideoObserveTask(String bvid) {
        if (mongoTemplate.exists(Query.query(Criteria.where("bvid").is(bvid)), VideoIntervalRecord.class)) {
            return new Result<>(ResultEnum.ALREADY_EXIST);
        }
        VideoIntervalRecord vir = new VideoIntervalRecord();
        vir.setInterval(86400);
        Date c = Calendar.getInstance().getTime();
        vir.setDate(c);
        vir.setNext(c);
        vir.setBvid(bvid);
        mongoTemplate.save(vir);
        return new Result<>(ResultEnum.SUCCEED);
    }

    public Result<?> addVideoObserveTask(Long aid) {
        if (mongoTemplate.exists(Query.query(Criteria.where("aid").is(aid)), VideoIntervalRecord.class)) {
            return new Result<>(ResultEnum.ALREADY_EXIST);
        }
        VideoIntervalRecord vir = new VideoIntervalRecord();
        vir.setInterval(86400);
        Date c = Calendar.getInstance().getTime();
        vir.setDate(c);
        vir.setNext(c);
        vir.setAid(aid);
        mongoTemplate.save(vir);
        return new Result<>(ResultEnum.SUCCEED);
    }

    private Video getVideoWithAuthorDataByCriteria(Criteria c) {
        Video v = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(c),
                Aggregation.lookup("author", "mid", "mid", "authorList"),
                Aggregation.project().andExpression("{data:0 , keyword:0, fansRate: 0, follows: 0, rank: 0}").as("authorList")
        ), Video.class, Video.class).getUniqueMappedResult();

        if (v == null) {
            return null;
        }
        if (v.getAuthorList() != null && v.getAuthorList().size() == 1) {
            v.setAuthor(v.getAuthorList().get(0));
        }
        v.setAuthorList(null);
        v.setKeyword(null);
        return v;
    }

    public Result<?> refreshVideoInterval(String bvid) {
        User u = UserUtils.getUser();
        return creditOperateHandle.doCreditOperate(u, CreditConstant.REFRESH_VIDEO_DATA_BY_BVID, () -> {
            return mongoTemplate.updateFirst(Query.query(Criteria.where("bvid").is(bvid)), Update.update("next", Calendar.getInstance().getTime()).addToSet("order", u.getId()), VideoIntervalRecord.class);
        });
    }
}
