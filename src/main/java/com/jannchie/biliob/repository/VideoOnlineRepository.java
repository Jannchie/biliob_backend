package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Video;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * @author jannchie
 */

public interface VideoOnlineRepository extends PagingAndSortingRepository<Video, ObjectId> {

}
