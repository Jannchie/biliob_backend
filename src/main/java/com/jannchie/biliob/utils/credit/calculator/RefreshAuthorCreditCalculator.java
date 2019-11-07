package com.jannchie.biliob.utils.credit.calculator;

import com.jannchie.biliob.utils.RedisOps;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author jannchie
 */
@Component
public class RefreshAuthorCreditCalculator extends AbstractCreditCalculator {

    private final RedisOps redisOps;

    @Autowired
    public RefreshAuthorCreditCalculator(MongoTemplate mongoTemplate, RedisOps redisOps) {
        super(mongoTemplate);
        this.redisOps = redisOps;
    }

    @Override
    ResponseEntity execute(Long id, ObjectId objectId) {
        redisOps.postAuthorCrawlTask(id, objectId);
        return null;
    }
}
