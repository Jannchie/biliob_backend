package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Site;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface SiteRepository
        extends MongoRepository<Site, ObjectId>, PagingAndSortingRepository<Site, ObjectId> {
}
