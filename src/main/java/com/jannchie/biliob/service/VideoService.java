package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


/**
 * @author jannchie
 */
@Service
public class VideoService {
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
    private final VideoRepository respository;

    @Autowired
    public VideoService(VideoRepository respository) {
        this.respository = respository;
    }

    public Video getVideoDetails(Long aid) {
        return respository.findByAid(aid);
    }

    public Video postVideoByAid(Long aid) {
        return respository.save(new Video(aid));
    }

    public Page<Video> getAuthor(Integer page, Integer pagesize) {
        return respository.findAll(PageRequest.of(page, pagesize));
    }
}
