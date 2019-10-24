package com.jannchie.biliob.utils.credit.calculator;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.utils.Result;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author jannchie
 */
@Component

public class PublishNewSupportCreditCalculator extends AbstractCreditCalculator {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PublishNewSupportCreditCalculator(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    ResponseEntity execute(Long id, ObjectId objectId) {
        Author author =
                mongoTemplate.findOne(Query.query(Criteria.where("mid").is(id)), Author.class, "author");
        if (author == null) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(objectId)), "user_record");
            return new ResponseEntity<>(new Result(ResultEnum.AUTHOR_NOT_FOUND), HttpStatus.ACCEPTED);
        } else if (author.getForceFocus() != null && author.getForceFocus()) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(objectId)), "user_record");
            return new ResponseEntity<>(
                    new Result(ResultEnum.AUTHOR_ALREADY_SUPPORTED), HttpStatus.ACCEPTED);
        }
        mongoTemplate.updateFirst(
                query(where("mid").is(id)), update("supporter", new HashMap(1)), "support");
        super.setExecuted(objectId);
        return null;
    }
}
