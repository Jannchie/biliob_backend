package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Video;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author jannchie
 */

@RepositoryRestResource(collectionResourceRel = "video", path = "video")
public interface VideoOnlineRepository extends PagingAndSortingRepository<Video, ObjectId> {

    Video findByAid(@Param("aid") Long aid);

    /**
     * @param pageable
     * @return 返回视频的分页信息
     */
    @Override
    Page<Video> findAll(Pageable pageable);

}
