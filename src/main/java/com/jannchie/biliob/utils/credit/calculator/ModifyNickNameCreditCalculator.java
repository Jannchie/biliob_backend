package com.jannchie.biliob.utils.credit.calculator;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author jannchie
 */
@Component
public class ModifyNickNameCreditCalculator extends AbstractCreditCalculator<String> {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ModifyNickNameCreditCalculator(MongoTemplate mongoTemplate) {
        super(mongoTemplate, CreditConstant.MODIFY_NAME);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    ResponseEntity execute(User user, String newName, ObjectId objectId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(user.getId())),
                Update.update("nickName", newName),
                "user");
        super.setExecuted(objectId);
        return null;
    }
}
