package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jannchie
 */

public interface AuthorRepository extends MongoRepository<Author, ObjectId>, PagingAndSortingRepository<Author, ObjectId> {

    /**
     * 获得确定mid的作者信息
     *
     * @param mid 作者id
     * @return 作者对象
     */
    Author findByMid(@Param("mid") Long mid);

    /**
     * 获得分页的作者信息，作者数据必须非空
     *
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> findAllByDataIsNotNull(Pageable pageable);

    /**
     * 通过mid搜索作者
     *
     * @param mid      作者id
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(value = "{'mid' : ?0}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> searchByMid(@Param("mid") Long mid, Pageable pageable);

    /**
     * 通过文本搜索作者
     *
     * @param text     文本
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(value = "{$or:[{name:{$regex:?0}},{official:{$regex:?0}}]}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Page<Author> search(String text, Pageable pageable);

    /**
     * get user favorite author
     *
     * @param mapsList author id map
     * @param of       page information
     * @return a slice of user favorite authors
     */
    @Query(value = "{$or:?0,data:{$ne:null}}", fields = "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1,'sex':1,'level':1}")
    Slice getFavoriteAuthor(ArrayList<HashMap<String, Long>> mapsList, PageRequest of);
}
