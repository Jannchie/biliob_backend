package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Bangumi;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author jannchie
 */
public interface BangumiRepository
    extends MongoRepository<Bangumi, ObjectId>, PagingAndSortingRepository<Bangumi, ObjectId> {

  /**
   * Find All Bangumi
   *
   * @param of page param
   * @return a slice
   */
  @Query(
      value = "{}",
      fields = "{ 'tag' : 1, 'title' : 1}"
  )
  Slice sliceBangumi(PageRequest of);
}
