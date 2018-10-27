package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * @author jannchie
 */
@Service
public interface VideoService {

    public Video getVideoDetails(Long aid);

    public Video postVideoByAid(Long aid);

    public Page<Video> getVideo(Long aid, String text, Integer page, Integer pagesize);

    public Slice<Video> getAuthorVideo(Long aid, Long mid, Integer page, Integer pagesize);
}
