package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.TracerTask;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface TracerRepository
        extends MongoRepository<TracerTask, ObjectId>,
        PagingAndSortingRepository<TracerTask, ObjectId> {
    /**
     * Find tracer by class name order bby start time desc.
     *
     * @param className   Task class Name
     * @param pageRequest Page request
     * @return the slice of tracer task.
     */
    Slice<TracerTask> findTracerByClassName(String className, PageRequest pageRequest);

    /**
     * Find tracer by class name order bby update time desc.
     *
     * @param className   Task class Name
     * @param pageRequest Page request
     * @return the slice of tracer task.
     */
    Slice<TracerTask> findTracerByClassNameOrderByUpdateTimeDesc(
            String className, PageRequest pageRequest);

    /**
     * Find tracer by class name order bby update time desc.
     *
     * @param className   Task class Name
     * @param status      Task status
     * @param pageRequest Page request
     * @return the slice of tracer task.
     */
    Slice<TracerTask> findTracerByClassNameAndStatus(
            String className, Integer status, PageRequest pageRequest);

    /**
     * Count tracer task number by status.
     *
     * @param className tracer task class name
     * @param status    tracer task status
     * @return the number of tracer tasks
     */
    Integer countTracerTaskByClassNameAndStatus(String className, Integer status);
}
