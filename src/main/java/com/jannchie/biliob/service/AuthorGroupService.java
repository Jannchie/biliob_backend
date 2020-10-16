package com.jannchie.biliob.service;

import com.jannchie.biliob.model.AuthorGroup;
import com.jannchie.biliob.model.GroupUpdateRecord;
import com.jannchie.biliob.utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jannchie
 */
@Service
public interface AuthorGroupService {

    /**
     * Init author list
     *
     * @param name list name
     * @param desc list desc
     * @param tag  list tag
     * @return result with list
     */
    Result<AuthorGroup> initAuthorList(String name, String desc, List<String> tag);

    /**
     * Set author list info
     *
     * @param id   list id
     * @param name list name
     * @param desc list desc
     * @param tag  list tag
     * @return result with list
     */
    Result<?> setAuthorListInfo(String id, String name, String desc, List<String> tag);

    /**
     * Delete author list
     *
     * @param objectId author list id
     * @return result
     */
    Result<?> deleteAuthorList(String objectId);

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
    List<AuthorGroup> listAuthorList(String keyword, Long page, Integer pageSize);

    /**
     * Get author list
     *
     * @param objectId author list id
     * @return author list
     */
    AuthorGroup getAuthorList(String objectId);


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

    List<AuthorGroup> listUserAuthorList(Integer page, Integer pageSize, int type);

    /**
     * Delete author from group
     *
     * @param gid group id
     * @param mid author id
     * @return result
     */
    Result<?> deleteAuthorFromGroup(String gid, Long mid);

    /**
     * Add author to group
     *
     * @param gid group id
     * @param mid author id
     * @return result
     */
    Result<?> addAuthorToGroup(String gid, Long mid);

    /**
     * list change log
     *
     * @param gid group id
     * @return change log
     */
    List<GroupUpdateRecord> listChangeLog(String gid);

    /**
     * edit author list
     *
     * @param id      group id
     * @param name    name
     * @param desc    desc
     * @param tagList tag list
     * @return result
     */
    Result<?> editAuthorList(String id, String name, String desc, List<String> tagList);
}
