package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Tracer;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/** @author jannchie */
@Repository
public interface TracerRepository
    extends MongoRepository<Tracer, ObjectId>, PagingAndSortingRepository<Tracer, ObjectId> {
  /**
   * Find tracer by class name order bby start time desc.
   *
   * @param className Task class Name
   * @param pageRequest Page request
   * @return the slcie of tracer task.
   */
  Slice<Tracer> findTracerByClassNameOrderByStartTimeDesc(
      String className, PageRequest pageRequest);
}
