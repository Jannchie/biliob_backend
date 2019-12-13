package com.jannchie.biliob.credit.handle;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.AuthorList;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.utils.Result;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Jannchie
 */
@Component
public class CreditHandle {
    final MongoTemplate mongoTemplate;

    public CreditHandle(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public ResponseEntity createAuthorList(User user, CreditConstant creditConstant, String title) {
        AuthorList authorList = new AuthorList(title, user.getName());
        mongoTemplate.save(authorList);
        String msg = String.format("%s (%s)", title, authorList.get("_id"));
        return getSuccessResponse(user, creditConstant, msg);
    }


    public ResponseEntity deleteAuthorList(User user, CreditConstant creditConstant, ObjectId id) {
        return ResponseEntity.ok(null);
    }

    public ResponseEntity alwaysFail(User user, CreditConstant creditConstant) {
        return null;
    }

    private ResponseEntity getSuccessResponse(User user, CreditConstant creditConstant) {
        ResponseEntity<Result> r;
        HashMap<String, Object> data = getResponseData(user, creditConstant);
        r = new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);
        return r;
    }

    private ResponseEntity getSuccessResponse(User user, CreditConstant creditConstant, String message) {
        ResponseEntity<Result> r;
        HashMap<String, Object> data = getResponseData(user, creditConstant, message);
        r = new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);
        return r;
    }

    private HashMap<String, Object> getResponseData(User user, CreditConstant creditConstant, String message) {
        HashMap<String, Object> data;
        data = new HashMap<>(3);
        data.put("exp", user.getExp() + creditConstant.getValue());
        data.put("credit", user.getCredit() + Math.abs(creditConstant.getValue()));
        data.put("msg", message);
        return data;
    }

    private HashMap<String, Object> getResponseData(User user, CreditConstant creditConstant) {
        HashMap<String, Object> data;
        data = new HashMap<>(3);
        data.put("exp", user.getExp() + creditConstant.getValue());
        data.put("credit", user.getCredit() + Math.abs(creditConstant.getValue()));
        return data;
    }
}
