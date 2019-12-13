package com.jannchie.biliob.service;

import com.jannchie.biliob.model.AuthorList;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author jannchie
 */
@Service
public interface AuthorListService {

    /**
     * 添加作者列表
     *
     * @param name 作者列表名称
     * @return 添加反馈
     */
    ResponseEntity postAuthorList(String name);

    /**
     * 根据点赞数，列出分页作者列表数据
     *
     * @param page     页号
     * @param pageSize 页大小
     * @return 作者列表数据
     */
    ArrayList<AuthorList> listAuthorListByLike(Integer page, Integer pageSize);

    /**
     * 根据创建日期，列出分页作者列表数据
     *
     * @param page     页号
     * @param pageSize 页大小
     * @return 作者列表数据
     */
    ArrayList<AuthorList> listAuthorListByDate(Integer page, Integer pageSize);

    /**
     * 根据更新日期，列出分页作者列表数据
     *
     * @param page     页号
     * @param pageSize 页大小
     * @return 作者列表数据
     */
    ArrayList<AuthorList> listAuthorByUpdatetime(Integer page, Integer pageSize);

    /**
     * 根据创建者，列出分页作者列表数据
     *
     * @param page     页号
     * @param pageSize 页大小
     * @return 作者列表数据
     */
    ArrayList<AuthorList> listAuthorListByUser(Integer page, Integer pageSize);

    /**
     * 列出个人喜欢的作者列表数据
     *
     * @param page     页号
     * @param pageSize 页大小
     * @return 作者列表数据
     */
    ArrayList<AuthorList> listAuthorListByUserLike(Integer page, Integer pageSize);

    /**
     * 列出作者列表详情
     *
     * @param id 作者列表ID
     * @return 作者列表详情
     */
    AuthorList getAuthorListDetail(ObjectId id);

    /**
     * 添加作者到现有作者列表中
     *
     * @param id  作者列表ID
     * @param mid 作者ID
     * @return 添加反馈
     */
    ResponseEntity addAuthorToAuthorList(ObjectId id, Long mid);

    /**
     * 从现有作者列表中移除作者
     *
     * @param id  作者列表ID
     * @param mid 作者ID
     * @return 移除反馈
     */
    ResponseEntity removeAuthorToAuthorList(ObjectId id, Long mid);

    /**
     * 从现有作者列表中移除作者
     *
     * @param id   作者列表ID
     * @param name 新的作者列表名称
     * @return 移除反馈
     */
    ResponseEntity updateAuthorListName(ObjectId id, String name);

    /**
     * 创建者功能
     * 删除整个作者列表
     *
     * @param id 作者列表ID
     * @return 删除反馈
     */
    ResponseEntity deleteAuthorList(ObjectId id);

    ResponseEntity getFail();
}
