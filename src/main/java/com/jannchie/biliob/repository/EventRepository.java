package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Event;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, ObjectId> {

    /**
     * Find all event by page.
     *
     * @param pageable page information
     * @return a page of author's event
     */
    @Override
    Page<Event> findAll(Pageable pageable);

    /**
     * Find event by author's ID.
     *
     * @param mid      author's ID
     * @param pageable page information.
     * @return a slice of specific author's events
     */
    Slice<Event> findByMid(@Param("mid") Integer mid, Pageable pageable);
}
