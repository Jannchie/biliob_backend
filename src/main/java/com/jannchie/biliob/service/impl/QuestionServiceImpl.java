package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.model.Question;
import com.jannchie.biliob.repository.QuestionRepository;
import com.jannchie.biliob.service.QuestionService;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.jannchie.biliob.constant.ResultEnum.PARAM_ERROR;

/**
 * @author jannchie
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LogManager.getLogger();
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * get a slice of handled question
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of questions
     */
    @Override
    public ResponseEntity getHandledQuestion(Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            return new ResponseEntity<>(new Result<>(PARAM_ERROR), HttpStatus.FORBIDDEN);
        }
        Slice<Question> questions =
                questionRepository.getHandledQuestions(PageRequest.of(page, pagesize));
        logger.info("获取已处理问题");
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    /**
     * get a slice of pending question
     *
     * @param page     page number
     * @param pagesize page size
     * @return a slice of questions
     */
    @Override
    public ResponseEntity getPendingQuestion(Integer page, Integer pagesize) {
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            return new ResponseEntity<>(new Result<>(PARAM_ERROR), HttpStatus.FORBIDDEN);
        }
        Slice<Question> questions =
                questionRepository.getPendingQuestions(PageRequest.of(page, pagesize));
        logger.info("获取未处理问题");
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}
