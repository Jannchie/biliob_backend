package com.jannchie.biliob.exception.handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.jannchie.biliob.exception.*;
import com.jannchie.biliob.utils.ExceptionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jannchie
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LogManager.getLogger(ControllerExceptionHandler.class);

    /**
     * 处理用户名已存在的异常
     *
     * @param userAlreadyExistException 用户已存在异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerUserAlreadyExistException(
            UserAlreadyExistException userAlreadyExistException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("用户名已经存在");
        // 记录下重名信息
        logger.info(userAlreadyExistException.getName());
        return ex;
    }

    /**
     * 处理用户名不存在的异常
     *
     * @param userNotExistException 用户不存在异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerUserNotExistException(UserNotExistException userNotExistException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("用户不存在");
        // 记录下不存在的用户信息
        logger.info(userNotExistException.getName());
        return ex;
    }

    /**
     * 处理已经关注过作者异常
     *
     * @param userAlreadyFavoriteAuthorException 已经关注过作者异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserAlreadyFavoriteAuthorException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerUserAlreadyFavoriteAuthor(
            UserAlreadyFavoriteAuthorException userAlreadyFavoriteAuthorException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("已经关注了该UP主");
        // 记录下不存在的用户信息
        logger.info(userAlreadyFavoriteAuthorException.getMid());
        return ex;
    }

    /**
     * 处理已经关注过视频异常
     *
     * @param userAlreadyFavoriteVideoException 已经关注过视频异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(UserAlreadyFavoriteVideoException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerUserAlreadyFavoriteVideo(
            UserAlreadyFavoriteVideoException userAlreadyFavoriteVideoException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("已经收藏了该视频");
        // 记录下不存在的用户信息
        logger.info(userAlreadyFavoriteVideoException.getAid());
        return ex;
    }

    /**
     * 处理已经观测过视频异常
     *
     * @param videoAlreadyFocusedException 已经观测过视频异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(VideoAlreadyFocusedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerVideoAlreadyFocusedException(
            VideoAlreadyFocusedException videoAlreadyFocusedException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("系统在观测该视频了");
        // 记录下不存在的用户信息
        logger.info(videoAlreadyFocusedException.getAid());
        return ex;
    }

    /**
     * 处理已经观测过作者异常
     *
     * @param authorAlreadyFocusedException 已经观测过作者异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(AuthorAlreadyFocusedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerAuthorAlreadyFocusedException(
            AuthorAlreadyFocusedException authorAlreadyFocusedException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("系统在观测该作者了");
        // 记录下不存在的用户信息
        logger.info(authorAlreadyFocusedException.getMid());
        return ex;
    }

    /**
     * 处理非法输入异常
     *
     * @param mismatchedInputException 非法输入异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(MismatchedInputException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerMismatchedInputException(
            MismatchedInputException mismatchedInputException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("非法输入");
        return ex;
    }

    /**
     * 处理非法输入异常
     *
     * @param httpMessageNotReadableException 非法输入异常的详细内容
     * @return 返回异常信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult handlerHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException) {
        ExceptionResult ex = new ExceptionResult();
        ex.setCode(400);
        ex.setMsg("非法输入");
        return ex;
    }
}
