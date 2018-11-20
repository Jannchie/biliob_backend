package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.Video;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public interface VideoRepository extends MongoRepository<Video, ObjectId>, PagingAndSortingRepository<Video, ObjectId> {

    /**
     * 通过aid寻找视频
     *
     * @param aid aid
     * @return 视频信息
     */
    Video findByAid(@Param("aid") Long aid);

    /**
     * 获得视频分页
     * @param pageable 分页
     * @return 返回视频的分页信息
     */
    @Query(value = "{data:{$ne:null}}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> findAllByAid(Pageable pageable);

    /**
     * 通过aid寻找视频（不包括data）
     *
     * @param aid      aid
     * @param pageable 分页
     * @return 视频页
     */
    @Query(value = "{'aid' : ?0}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> searchByAid(@Param("aid") Long aid, Pageable pageable);

    /**
     * 通过文本搜索视频
     *
     * @param text     文本
     * @param pageable 分页
     * @return 视频页
     */
    @Query(value = "{$or:[{channel:{$regex:?0}},{author:{$regex:?0}},{title:{$regex:?0}}]}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> searchByText(String text, Pageable pageable);

    /**
     * 寻找Data不是空的视频
     *
     * @param pageable 分页
     * @return 视频页
     */
    @Query(fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Page<Video> findByDataIsNotNull(Pageable pageable);

    /**
     * 获得作者的其他视频
     * @param aid 视频id
     * @param mid 作者id
     * @param pageable 分页
     * @return 视频列表切片
     */
    @Query(value = "{aid:{$ne:?0},mid:?1}", fields = "{'title' : 1, 'aid' : 1, 'mid' : 1,'channel':1}")
    Slice<Video> findAuthorOtherVideo(Long aid, Long mid, Pageable pageable);

    /**
     * get user favorite video
     *
     * @param aids video id
     * @param of   page information
     * @return a slice of user favorite videos
     */
    @Query(value = "{$or:?0,data:{$ne:null}}", fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}")
    Slice getFavoriteVideo(ArrayList<HashMap<String, Long>> aids, PageRequest of);

}
