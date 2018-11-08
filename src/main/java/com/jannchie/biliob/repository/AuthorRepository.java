package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import java.util.ArrayList;
import java.util.List;

/**
 * @author jannchie
 */

public interface AuthorRepository extends MongoRepository<Author, ObjectId>, PagingAndSortingRepository<Author, ObjectId> {

    Author findByMid(@Param("mid") Long mid);

    @Override
    @Query(value = "{data:{$ne:null}}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> findAll(Pageable pageable);

    @Query(value = "{'mid' : ?0}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> searchByMid(@Param("mid") Long mid, Pageable pageable);

    @Query(value = "{$or:[{name:{$regex:?0}},{official:{$regex:?0}}]}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> search(String text, Pageable pageable);

}
