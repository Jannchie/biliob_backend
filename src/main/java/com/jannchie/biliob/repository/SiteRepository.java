package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Site;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author jannchie
 */
public interface SiteRepository
    extends MongoRepository<Site, ObjectId>, PagingAndSortingRepository<Site, ObjectId> {
}
