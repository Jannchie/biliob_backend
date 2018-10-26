package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


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

    public Page<Video> getVideo(Long aid, String text, Integer page, Integer pagesize) {
        if (!(aid == -1)) {
            logger.info("[GET]searchByAid");
            return respository.searchByAid(aid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        } else if (!Objects.equals(text, "")) {
            logger.info("[GET]searchByText");
            return respository.searchByText(text, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        } else {
            logger.info("[GET]findAll");
            return respository.findByDataIsNotNull(PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
        }
    }

    public Slice<Video> getAuthorVideo(Long aid, Long mid, Integer page, Integer pagesize) {
        return respository.findAuthorVideo(aid, mid, PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, "data.0.view")));
    }
}
