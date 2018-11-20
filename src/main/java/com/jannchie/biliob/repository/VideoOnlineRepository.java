package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Video;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author jannchie
 */
public interface VideoOnlineRepository extends PagingAndSortingRepository<Video, ObjectId> {
}
