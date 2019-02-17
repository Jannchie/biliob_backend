package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Donghua;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface DonghuaRepository
    extends MongoRepository<Donghua, ObjectId>, PagingAndSortingRepository<Donghua, ObjectId> {

  /**
   * Find All Donghua
   *
   * @param of page param
   * @return a slice
   */
  @Query(
      value = "{}",
      fields = "{'data':0}"
  )
  Slice sliceDonghua(PageRequest of);
}
