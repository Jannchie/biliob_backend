package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.RealTimeFans;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** @author jannchie */
@Repository
public interface RealTimeFansRepository extends MongoRepository<RealTimeFans, ObjectId> {
  /**
   * find real time fans data by mid
   *
   * @param mid author id
   * @return list of real time fans data
   */
  List<RealTimeFans> findTop180ByMidOrderByDatetimeDesc(Long mid);
}
