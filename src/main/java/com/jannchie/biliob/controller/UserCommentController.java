package com.jannchie.biliob.controller;

import com.jannchie.biliob.model.Comment;
import com.jannchie.biliob.service.UserCommentService;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jannchie
 */
@RestController
public class UserCommentController {
    private static final Logger logger = LogManager.getLogger(UserCommentController.class);
    private UserCommentService userCommentService;

    @Autowired
    public UserCommentController(UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/comment")
    public List<Comment> getComments(@RequestParam(name = "path") String path,
                                     @RequestParam(name = "ps", defaultValue = "20") Integer pageSize,
                                     @RequestParam(name = "p", defaultValue = "0") Integer page,
                                     @RequestParam(name = "s", defaultValue = "0") Integer sort) {
        return userCommentService.listComments(path, page, pageSize, sort);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/user/comment")
    public ResponseEntity<Result<Comment>> postComment(@RequestBody @Valid Comment comment) {
        return userCommentService.postComment(comment);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/comment/{commentId}/like")
    public Result<?> likeComment(@PathVariable("commentId") String commentId) {
        return userCommentService.likeComment(commentId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/api/user/comment/{commentId}/dislike")
    public ResponseEntity<Result<?>> dislikeComment(@PathVariable("commentId") String commentId) {
        return userCommentService.dislikeComment(commentId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/user/comment/{commentId}")
    public ResponseEntity<Result<?>> deleteComment(@PathVariable("commentId") String commentId) {
        return userCommentService.deleteComment(commentId);
    }
}
