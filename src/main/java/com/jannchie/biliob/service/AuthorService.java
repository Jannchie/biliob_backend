package com.jannchie.biliob.service;

import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.exception.UserAlreadyFavoriteAuthorException;
import com.jannchie.biliob.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
public interface AuthorService {

    /**
     * 获取作者详情
     *
     * @param mid  作者id
     * @return 作者详细信息
     */
    Author getAuthorDetails(Long mid);

    /**
     * 添加作者追踪
     *
     * @param mid 作者id
     * @throws UserAlreadyFavoriteAuthorException 用户已经观测该作者
     * @throws AuthorAlreadyFocusedException      作者已经在系统中
     */
    void postAuthorByMid(Long mid) throws UserAlreadyFavoriteAuthorException, AuthorAlreadyFocusedException;

    /**
     * 获取作者页
     *
     * @param mid      作者id
     * @param text     文本
     * @param page     页数
     * @param pagesize 页大小
     * @return 作者页
     */
    Page<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize);
}
