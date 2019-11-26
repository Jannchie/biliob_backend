package com.jannchie.biliob.service;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.utils.MySlice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jannchie
 */
@Service
public interface AuthorService {

    /**
     * 获取作者详情
     *
     * @param mid  作者id
     * @param type default: get original data; 1: get aggregated data
     * @return 作者详细信息
     */
    Author getAuthorDetails(Long mid, Integer type);

    /**
     * get aggregated data of author
     *
     * @param mid author id
     * @return author
     */
    Author getAggregatedData(Long mid);

    /**
     * 添加作者追踪
     *
     * @param mid 作者id
     * @throws UserAlreadyFavoriteAuthorException 用户已经观测该作者
     * @throws AuthorAlreadyFocusedException      作者已经在系统中
     */
    void postAuthorByMid(Long mid)
            throws UserAlreadyFavoriteAuthorException, AuthorAlreadyFocusedException;

    /**
     * 获取作者页
     *
     * @param mid      作者id
     * @param text     文本
     * @param page     页数
     * @param pagesize 页大小
     * @param sort     sort field
     * @return 作者页
     */
    MySlice<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize, Integer sort);

    /**
     * get a list of author's fans increase rate.
     *
     * @return list of author rate of fans increase.
     */
    ResponseEntity listFansIncreaseRate();

    /**
     * get a list of author's fans decrease rate.
     *
     * @return list of author rate of fans decrease.
     */
    ResponseEntity listFansDecreaseRate();

    /**
     * get specific author's fans rate
     *
     * @param mid author id
     * @return list of fans
     */
    ResponseEntity listFansRate(Long mid);

    /**
     * get author information exclude history data.
     *
     * @param mid author id
     * @return author
     */
    Author getAuthorInfo(Long mid);

    /**
     * list real time data
     *
     * @param aMid one author id
     * @param bMid another author id
     * @return Real time fans responseEntity
     */
    ResponseEntity getRealTimeData(Long aMid, Long bMid);

    /**
     * list author tag
     *
     * @param mid   author id
     * @param limit length of result list
     * @return tag list
     */
    List listAuthorTag(Long mid, Integer limit);

    /**
     * list relate author by author id
     *
     * @param mid   author id
     * @param limit length of result list
     * @return author list
     */
    List listRelatedAuthorByMid(Long mid, Integer limit);

    /**
     * get top author data
     *
     * @return top author data response entity
     */
    ResponseEntity getTopAuthor();

    /**
     * get latest top author data
     *
     * @return latest top author data response entity
     */
    ResponseEntity getLatestTopAuthorData();

    /**
     * upsert author frequency
     *
     * @param mid         author id
     * @param interval    interval of every crawl
     * @param refreshNext whether refresh next
     */
    void upsertAuthorFreq(Long mid, Integer interval, boolean refreshNext);

    /**
     * upsert author frequency
     *
     * @param mid      author id
     * @param interval interval of every crawl
     */
    void upsertAuthorFreq(Long mid, Integer interval);

    /**
     * 获取作者ID以及累计访问次数的对象列表
     *
     * @param days  天数限制
     * @param limit 数量限制
     * @return 作者ID以及累计访问次数的对象列表
     */
    List<Map> listMostVisitAuthorId(Integer days, Integer limit);

    /**
     * 更新观测频率
     */
    void updateObserveFreq();

    /**
     * 获取大于指定粉丝数的作者ID
     *
     * @param gt fans limit
     * @return author list
     */
    List<Author> getAuthorFansGt(int gt);

    /**
     * 获取热搜作者
     *
     * @return 热搜作者列表
     */
    List listHotAuthor();
}
