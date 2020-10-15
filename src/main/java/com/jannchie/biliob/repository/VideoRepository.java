package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Video;
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
import java.util.Date;
import java.util.HashMap;

/**
 * @author jannchie
 */
@Repository
public interface VideoRepository
        extends MongoRepository<Video, ObjectId>, PagingAndSortingRepository<Video, ObjectId> {

    /**
     * 通过aid寻找视频
     *
     * @param aid aid
     * @return 视频信息
     */
    Video findByAid(@Param("aid") Long aid);

    /**
     * 获得视频分页
     *
     * @param pageable 分页
     * @return 返回视频的分页信息
     */
    @Query(
            value = "{data:{$ne:null}}",
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1}"
    )
    Slice<Video> findAllByAid(Pageable pageable);

    /**
     * 通过aid寻找视频（不包括data）
     *
     * @param aid      aid
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            value = "{'aid' : ?0 }",
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1, 'datetime':1}"
    )
    Slice<Video> searchByAid(@Param("aid") Long aid, Pageable pageable);

    /**
     * 通过文本搜索视频
     *
     * @param text     文本
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            value = "{'keyword': ?0}",
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1}"
    )
    Slice<Video> searchByText(String text, Pageable pageable);

    /**
     * `通过关键字列表搜索视频
     *
     * @param keyword  关键字
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            value = "{'keyword': {'$all': ?0}}",
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1}"
    )
    Slice<Video> findByKeywordContaining(String[] keyword, Pageable pageable);

    /**
     * `通过关键字搜索视频
     *
     * @param keyword  关键字
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            value = "{'keyword': ?0}",
            fields = "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1}"
    )
    Slice<Video> findByOneKeyword(String keyword, Pageable pageable);

    /**
     * 寻找Data不是空的视频
     *
     * @param date     date
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1, 'datetime': 1}"
    )
    Slice<Video> findAllByDatetimeGreaterThan(Date date, Pageable pageable);

    /**
     * 寻找Data不是空的视频
     *
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1, 'datetime': 1}"
    )
    Slice<Video> findAllByDataIsNotNull(Pageable pageable);


    /**
     * 寻找所有视频
     *
     * @param pageable 分页
     * @return 视频页
     */
    @Query(
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus':1, 'tag': 1, 'datetime': 1}"
    )
    Slice<Video> findVideoBy(Pageable pageable);

    /**
     * 获得作者的其他视频
     *
     * @param aid      视频id
     * @param mid      作者id
     * @param pageable 分页
     * @return 视频列表切片
     */
    @Query(
            value = "{aid:{$ne:?0},mid:?1}",
            fields = "{'title' : 1, 'aid' : 1, 'mid' : 1,'channel':1,'datetime':1, 'pic':1}"
    )
    Slice<Video> findAuthorOtherVideo(Long aid, Long mid, Pageable pageable);

    /**
     * Find author's top video.
     *
     * @param mid      author id
     * @param pageable page information
     * @return a slice of author's top video.
     */
    @Query(
            value = "{mid:?0}",
            fields = "{'title' : 1, 'aid' : 1, 'mid' : 1,'channel':1,'datetime':1, 'pic':1}"
    )
    Slice<Video> findAuthorTopVideo(Long mid, Pageable pageable);

    /**
     * get user favorite video
     *
     * @param aids video id
     * @param of   page information
     * @return a slice of user favorite videos
     */
    @Query(
            value = "{$or:?0,data:{$ne:null}}",
            fields =
                    "{ 'pic' : 1, 'mid' : 1, 'author' : 1, 'authorName' : 1, 'bvid': 1, 'channel' : 1, 'title' : 1, 'aid' : 1, 'focus': 1, 'tag': 1}"
    )
    Slice<?> getFavoriteVideo(ArrayList<HashMap<String, Long>> aids, PageRequest of);
}
