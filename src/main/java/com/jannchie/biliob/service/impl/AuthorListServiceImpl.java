package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.credit.handle.CreditHandle;
import com.jannchie.biliob.model.AuthorList;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.service.AuthorListService;
import com.jannchie.biliob.utils.LoginChecker;
import com.jannchie.biliob.utils.Result;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author jannchie
 */
@Service
public class AuthorListServiceImpl implements AuthorListService {

    final MongoTemplate mongoTemplate;
    final CreditHandle creditHandle;

    @Autowired
    public AuthorListServiceImpl(MongoTemplate mongoTemplate, CreditHandle creditHandle) {
        this.mongoTemplate = mongoTemplate;
        this.creditHandle = creditHandle;
    }

    @Override
    public ResponseEntity postAuthorList(String name) {
        User user = LoginChecker.checkInfo();
        return creditHandle.createAuthorList(user, CreditConstant.ADD_AUTHOR_LIST, name);
    }


    @Override
    public ArrayList<AuthorList> listAuthorListByLike(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public ArrayList<AuthorList> listAuthorListByDate(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public ArrayList<AuthorList> listAuthorByUpdatetime(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public ArrayList<AuthorList> listAuthorListByUser(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public ArrayList<AuthorList> listAuthorListByUserLike(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public AuthorList getAuthorListDetail(ObjectId id) {
        return null;
    }

    @Override
    public ResponseEntity addAuthorToAuthorList(ObjectId id, Long mid) {
        return null;
    }

    @Override
    public ResponseEntity removeAuthorToAuthorList(ObjectId id, Long mid) {
        return null;
    }

    @Override
    public ResponseEntity updateAuthorListName(ObjectId id, String name) {
        return null;
    }

    @Override
    public ResponseEntity deleteAuthorList(ObjectId id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)));
        return ResponseEntity.ok(new Result(ResultEnum.SUCCEED));
    }

    @Override
    public ResponseEntity getFail() {
        return creditHandle.alwaysFail(LoginChecker.checkInfo(), CreditConstant.ALWAYS_FAIL);
    }
}
