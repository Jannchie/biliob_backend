package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Video;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;

/**
 * @author jannchie
 */

public interface VideoRepository extends MongoRepository<Video, ObjectId>, PagingAndSortingRepository<Video, ObjectId> {

    Video findByAid(@Param("aid") Long aid);

    /**
     * @param pageable
     * @return 返回视频的分页信息
     */
    @Query(value = "{data:{$ne:null}}", fields = "{ ‘data.1’:1, 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> findAllByAid(Pageable pageable);

    @Query(value = "{'aid' : ?0}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> searchByAid(@Param("aid") Long aid, Pageable pageable);

    @Query(value = "{$or:[{channel:{$regex:?0}},{author:{$regex:?0}},{title:{$regex:?0}}]}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> searchByText(String text, Pageable pageable);

    @Query(fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> findByDataIsNotNull(Pageable pageable);

    @Query(value = "{aid:{$ne:?0},mid:?1}", fields = "{'title' : 1, 'aid' : 1, 'mid' : 1,'channel':1}")
    Slice<Video> findAuthorOtherVideo(Long aid, Long mid, Pageable pageable);
}
