package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.UserRecord;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/** @author jannchie */
@Repository
public interface UserRecordRepository
    extends MongoRepository<UserRecord, ObjectId>,
        PagingAndSortingRepository<UserRecord, ObjectId> {

  /**
   * Get user record slice order by datetime desc.
   *
   * @param userName user's name
   * @param pageable page information
   * @return the slice of user
   */
  Slice<UserRecord> findByUserNameOrderByDatetimeDesc(String userName, Pageable pageable);
}
