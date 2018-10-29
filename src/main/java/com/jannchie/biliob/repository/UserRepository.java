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
<<<<<<<Updated upstream
=======

    Stashed changes

    User findByName(String name);

    @Query(value = "{name:?0}", fields = "{password:0}")
    User getUserInfo(String name);
>>>>>>>

    User findAllByName(String name);
}
