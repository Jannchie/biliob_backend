package com.jannchie.biliob.service;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Jannchie
 */
@Service
public class IndexServiceV2 {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoClient mongoClient;

    public Document getIndex(String keyword) {
        return null;
    }
}
