package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author jannchie
 */

public interface UserRepository extends PagingAndSortingRepository<User, ObjectId> {

    Integer countByName(String name);

    User findByName(String name);

}
