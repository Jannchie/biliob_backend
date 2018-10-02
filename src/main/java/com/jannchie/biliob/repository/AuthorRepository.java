package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jannchie
 */

@RepositoryRestResource(collectionResourceRel = "author", path = "author")
public interface AuthorRepository extends PagingAndSortingRepository<Author, ObjectId> {

    Author findByMid(@Param("mid") Long mid);

    @Override
    @Query(value = "{}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1}")
    Page<Author> findAll(Pageable pageable);

}
