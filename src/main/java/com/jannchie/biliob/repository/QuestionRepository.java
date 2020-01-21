package com.jannchie.biliob.repository;

import com.jannchie.biliob.model.Question;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jannchie
 */
@Repository
public interface QuestionRepository
        extends MongoRepository<Question, ObjectId>, PagingAndSortingRepository<Question, ObjectId> {
    /**
     * get all pending questions and load in a slice.
     *
     * @param pageable page information.
     * @return question slice
     */
    @Query(value = "{ status: '未处理' }")
    Slice<Question> getPendingQuestions(Pageable pageable);

    /**
     * get all handled questions and load in a slice.
     *
     * @param pageable page information.
     * @return question slice
     */
    @Query(value = "{ status: '已处理' }")
    Slice<Question> getHandledQuestions(Pageable pageable);
}
