package com.jannchie.biliob.service;

import com.jannchie.biliob.model.AuthorList;
import com.jannchie.biliob.utils.Result;
import com.mongodb.client.result.UpdateResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jannchie
 */
@Service
public interface AuthorListService {

    /**
     * Init author list
     *
     * @param name list name
     * @param desc list desc
     * @param tag  list tag
     * @return result with list
     */
    Result<AuthorList> initAuthorList(String name, String desc, List<String> tag);

    /**
     * Set author list info
     *
     * @param id   list id
     * @param name list name
     * @param desc list desc
     * @param tag  list tag
     * @return result with list
     */
    Result<UpdateResult> setAuthorListInfo(String id, String name, String desc, List<String> tag);

    /**
     * Delete author list
     *
     * @param objectId author list id
     * @return result
     */
    Result<?> deleteAuthorList(String objectId);

    /**
     * Add author to author list
     *
     * @param objectId author list id
     * @param mid      author mid
     * @return result
     */
    Result<?> addAuthorToAuthorList(String objectId, Long mid);

    /**
     * Remove author from author list
     *
     * @param objectId author list id
     * @param mid      author mid
     * @return result
     */
    Result<?> removeAuthorFromAuthorList(String objectId, Long mid);

    /**
     * Star author list
     *
     * @param objectId author list id
     * @return result
     */
    Result<?> starAuthorList(String objectId);

    /**
     * Star author list
     *
     * @param objectId author list id
     * @return result
     */
    Result<?> forkAuthorList(String objectId);

    /**
     * List author list
     *
     * @param keyword  keyword
     * @param page     page
     * @param pageSize page size
     * @return list of author list
     */
    List<AuthorList> listAuthorList(String keyword, Long page, Integer pageSize);

    /**
     * Get author list
     *
     * @param objectId author list id
     * @return author list
     */
    AuthorList getAuthorList(String objectId);


    /**
     * unstar author list
     *
     * @param objectId author list id
     * @return result
     */
    Result<?> unstarAuthorList(String objectId);

    /**
     * Get user author list
     *
     * @param page     page
     * @param pageSize page size
     * @param type     type
     * @return author list
     */

    List<AuthorList> listUserAuthorList(Integer page, Integer pageSize, int type);
}
