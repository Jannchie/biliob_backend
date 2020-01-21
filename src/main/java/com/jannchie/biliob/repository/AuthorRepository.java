package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jannchie
 */
@Repository
public interface AuthorRepository
        extends MongoRepository<Author, ObjectId>, PagingAndSortingRepository<Author, ObjectId> {

    /**
     * get author slice by keywords
     *
     * @param keyword  a array list of keyword
     * @param pageable page information
     * @return the slice of author
     */
    @Query(
            fields =
                    "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1, 'forceFocus':1, 'sex':1,'level':1}"
    )
    Slice<Author> findByKeywordContaining(String[] keyword, Pageable pageable);

    /**
     * 获得确定mid的作者信息
     *
     * @param mid 作者id
     * @return 作者对象
     */
    @Query(fields = "{ 'fansRate' : 0}")
    Author findByMid(@Param("mid") Long mid);


    /**
     * get author exclude data and fansRate
     *
     * @param mid 作者id
     * @return 作者对象
     */
    @Query(fields = "{ 'fansRate': 0, 'data': 0}")
    Author findAuthorByMid(@Param("mid") Long mid);


    /**
     * 获得分页的作者信息，作者数据必须非空
     *
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(
            fields =
                    "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1, 'forceFocus':1, 'sex':1,'level':1}"
    )
    Slice<Author> findAllByDataIsNotNull(Pageable pageable);

    /**
     * 通过mid搜索作者
     *
     * @param mid      作者id
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(
            value = "{'mid' : ?0}",
            fields =
                    "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1, 'forceFocus':1, 'sex':1,'level':1}"
    )
    Slice<Author> searchByMid(@Param("mid") Long mid, Pageable pageable);

    /**
     * 通过文本搜索作者
     *
     * @param text     文本
     * @param pageable 分页
     * @return 一页作者
     */
    @Query(
            value = "{$or:[{name:{$regex:?0}},{official:{$regex:?0}}]}",
            fields =
                    "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1, 'forceFocus':1, 'sex':1,'level':1}"
    )
    Slice<Author> search(String text, Pageable pageable);

    /**
     * get user favorite author
     *
     * @param mapsList author id map
     * @param of       page information
     * @return a Slice of user favorite authors
     */
    @Query(
            value = "{$or:?0,data:{$ne:null}}",
            fields =
                    "{ 'name' : 1, 'mid' : 1, 'face' : 1, 'official' : 1, 'focus':1, 'forceFocus':1, 'sex':1, 'level':1}"
    )
    Slice getFavoriteAuthor(ArrayList<HashMap<String, Long>> mapsList, PageRequest of);

    /**
     * listTopIncreaseRate
     *
     * @param of page information
     * @return a Slice of top fans increase rate.
     */
    @Query(value = "{cRate:{$ne:null}}", fields = "{ 'data': 0, 'fansRate': 0 }")
    Slice<Author> listTopIncreaseRate(PageRequest of);

    /**
     * get fans rate
     *
     * @param mid author's id
     * @return a author
     */
    @Query(value = "{mid:?0}", fields = "{ 'fansRate':1}")
    Author getFansRate(Long mid);
}
