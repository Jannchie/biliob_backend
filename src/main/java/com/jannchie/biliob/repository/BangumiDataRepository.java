package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.BangumiData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface BangumiDataRepository
        extends MongoRepository<BangumiData, ObjectId>, PagingAndSortingRepository<BangumiData, ObjectId> {
}
