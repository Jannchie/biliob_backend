package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.exception.UserAlreadyFavoriteVideoException;
import com.jannchie.biliob.exception.VideoAlreadyFocusedException;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.repository.VideoRepository;
import com.jannchie.biliob.service.UserService;
import com.jannchie.biliob.service.VideoService;
import com.jannchie.biliob.utils.Message;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author jannchie
 */
@Service
public class VideoServiceImpl implements VideoService {
    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
    private final VideoRepository respository;
    private final UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public VideoServiceImpl(VideoRepository respository, UserRepository userRepository, UserService userService) {
        this.respository = respository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Video getVideoDetails(Long aid) {
        logger.info(aid);
        return respository.findByAid(aid);
    }

    @Override
    public ResponseEntity<Message> postVideoByAid(Long aid) throws VideoAlreadyFocusedException, UserAlreadyFavoriteVideoException {
        User user = userService.addFavoriteVideo(aid);
        logger.info(aid);
        logger.info(user.getName());
        if (respository.findByAid(aid) != null) {
            throw new VideoAlreadyFocusedException(aid);
        }
        respository.save(new Video(aid));
        return new ResponseEntity<>(new Message(200, "观测视频成功"), HttpStatus.OK);
    }

    @Override
    public Page<Video> getVideo(Long aid, String text, Integer page, Integer pagesize) {
        if (!(aid == -1)) {
            logger.info(aid);
            return respository.searchByAid(aid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        } else if (!Objects.equals(text, "")) {
            logger.info(text);
            return respository.searchByText(text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        } else {
            logger.info("获取全部视频数据");
            return respository.findByDataIsNotNull(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        }
    }

    @Override
    public Slice<Video> getAuthorVideo(Long aid, Long mid, Integer page, Integer pagesize) {
        return respository.findAuthorOtherVideo(aid, mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
    }
}
