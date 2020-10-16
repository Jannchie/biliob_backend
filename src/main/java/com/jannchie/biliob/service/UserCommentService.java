package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Comment;
import com.jannchie.biliob.utils.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jannchie
 */
@Service
public interface UserCommentService {

    /**
     * list comments of path
     *
     * @param path     path
     * @param page     page
     * @param pageSize pagesize
     * @param sort     sort
     * @return comment list
     */
    List<Comment> listComments(String path, Integer page, Integer pageSize, Integer sort);

    /**
     * post comment
     *
     * @param comment post comment
     * @return result with comment
     */
    Result<Comment> postComment(Comment comment);

    /**
     * like
     *
     * @param commentId operate comment id
     * @return operate result
     */
    Result<?> likeComment(String commentId);

    /**
     * dislike
     *
     * @param commentId operate comment id
     * @return operate result
     */
    ResponseEntity<Result<?>> dislikeComment(String commentId);

    /**
     * delete
     *
     * @param commentId operate comment id
     * @return operate result
     */
    ResponseEntity<Result<?>> deleteComment(String commentId);
}
