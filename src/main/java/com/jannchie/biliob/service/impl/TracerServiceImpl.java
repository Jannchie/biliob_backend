package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.TaskStatusEnum;
import com.jannchie.biliob.repository.TracerRepository;
import com.jannchie.biliob.service.TracerService;
import com.jannchie.biliob.utils.RedisOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jannchie.biliob.constant.TaskTypeEnum.GET_ALL;
import static com.jannchie.biliob.constant.TaskTypeEnum.GET_RUNNING;

/**
 * @author jannchie
 */
@Service
public class TracerServiceImpl implements TracerService {

    private static final Integer MAX_ONLINE_PLAY_RANGE = 30;
    private static final Integer HOUR_IN_DAY = 24;

    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
    private final MongoTemplate mongoTemplate;
    private final TracerRepository tracerRepository;
    private final RedisOps redisOps;

    @Autowired
    public TracerServiceImpl(
            MongoTemplate mongoTemplate, TracerRepository tracerRepository, RedisOps redisOps) {
        this.mongoTemplate = mongoTemplate;
        this.tracerRepository = tracerRepository;
        this.redisOps = redisOps;
    }

    /**
     * It is the function to get authors' queue status.
     *
     * @return The authors' queue status.
     */
    @Override
    public ResponseEntity getAuthorQueueStatus() {
        Map<String, Long> result = new HashMap<>(1);
        Long authorCrawlTasksQueueLength = redisOps.getAuthorQueueLength();
        result.put("length", authorCrawlTasksQueueLength);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * It is the function to get videos' queue status.
     *
     * @return The videos' queue status.
     */
    @Override
    public ResponseEntity getVideoQueueStatus() {
        Map<String, Long> result = new HashMap<>(1);
        Long videoCrawlTasksQueueLength = redisOps.getVideoQueueLength();
        result.put("length", videoCrawlTasksQueueLength);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get the slice of exists task of the system.
     *
     * <p>It is able to get the status of Biliob scheduler and Biliob spider.
     *
     * @param page     The page number of the task slice.
     * @param pagesize The page size of the task slice.
     * @return tTe slice of exists task of the system.
     */
    @Override
    public ResponseEntity sliceExistsTask(Integer page, Integer pagesize) {
        return new ResponseEntity<>(
                tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
                        "ExistsTask", PageRequest.of(page, pagesize)),
                HttpStatus.OK);
    }

    /**
     * Get the slice of progress task of the system.
     *
     * <p>It is able to get the status of Biliob link generate task.
     *
     * @param page     The page number of the task slice.
     * @param pagesize The page size of the task slice.
     * @return tTe slice of exists task of the system.
     */
    @Override
    public ResponseEntity sliceProgressTask(Integer page, Integer pagesize) {
        return new ResponseEntity<>(
                tracerRepository.findTracerByClassNameOrderByUpdateTimeDesc(
                        "ProgressTask", PageRequest.of(page, pagesize)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity sliceSpiderTask(Integer page, Integer pagesize, Integer type) {
        if (type.equals(GET_ALL.value)) {
            return new ResponseEntity<>(
                    tracerRepository.findTracerByClassName("SpiderTask", PageRequest.of(page, pagesize)),
                    HttpStatus.OK);
        } else if (type.equals(GET_RUNNING.value)) {
            return new ResponseEntity<>(
                    tracerRepository.findTracerByClassNameAndStatus(
                            "SpiderTask", TaskStatusEnum.ALIVE.value, PageRequest.of(page, pagesize)),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                tracerRepository.findTracerByClassName("SpiderTask", PageRequest.of(page, pagesize)),
                HttpStatus.OK);
    }

    /**
     * Get the data for the dashboard page.
     *
     * @return tTe slice of exists task of the system.
     */
    @Override
    public ResponseEntity getDashboardData() {
        Map<String, Object> resultMap = new HashMap<>(10);

        getCrawlCountAggregationData(resultMap);
        getSumSpiderCountData(resultMap);
        getBucketUserCreditList(resultMap);
        getCheckedInCount(resultMap);
        getUserCount(resultMap);
        getLatestProgressTask(resultMap);
        getWeeklyCheckIn(resultMap);
        getMonthlySignIn(resultMap);
        getRecordCount(resultMap);
        getLatestSpiderTask(resultMap);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getLatestProgressTaskResponse() {
        Map<String, Object> resultMap = new HashMap<>(2);
        getLatestProgressTask(resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * Get latest spider task response.
     *
     * @return response entity of latest spider task.
     */
    @Override
    public ResponseEntity getLatestSpiderTaskResponse() {
        Map<String, Object> resultMap = new HashMap<>(2);
        getLatestSpiderTask(resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getHistoryQueueStatus() {
        List data = mongoTemplate.findAll(Map.class, "spider_queue_status");
        return ResponseEntity.ok(data);
    }

    private void getLatestSpiderTask(Map<String, Object> resultMap) {
        resultMap.put("authorCrawlLength", redisOps.getAuthorQueueLength());
        resultMap.put("videoCrawlLength", redisOps.getVideoQueueLength());
    }

    private void getBucketUserCreditList(Map<String, Object> resultMap) {
        Aggregation bucketUserCreditAggregation =
                Aggregation.newAggregation(Aggregation.bucketAuto("exp", 20));

        AggregationResults<Map> bucketUserCreditAggregationResult =
                mongoTemplate.aggregate(bucketUserCreditAggregation, "user", Map.class);

        ArrayList bucketUserCreditList =
                (ArrayList) bucketUserCreditAggregationResult.getRawResults().get("results");

        resultMap.put("userBucketResult", bucketUserCreditList);
    }

    private void getSumSpiderCountData(Map<String, Object> resultMap) {
        Integer sumSpiderCount =
                tracerRepository.countTracerTaskByClassNameAndStatus(
                        "SpiderTask", TaskStatusEnum.ALIVE.value);
        resultMap.put("sumSpiderCount", sumSpiderCount);
    }

    private void getCrawlCountAggregationData(Map<String, Object> resultMap) {
        Aggregation crawlCountAggregation =
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("class_name").is("SpiderTask")),
                        Aggregation.group("class_name").sum("crawl_count").as("sumCrawlCount"));

        AggregationResults<Map> crawlCountAggregationResult =
                mongoTemplate.aggregate(crawlCountAggregation, "tracer", Map.class);
        resultMap.putAll(Objects.requireNonNull(crawlCountAggregationResult.getUniqueMappedResult()));
    }

    private void getCheckedInCount(Map<String, Object> resultMap) {
        Long count = mongoTemplate.count(new Query(), "check_in");
        resultMap.put("checkedInCount", count);
    }

    private void getUserCount(Map<String, Object> resultMap) {
        Long count = mongoTemplate.count(new Query(), "user");
        resultMap.put("userCount", count);
    }

    private void getLatestProgressTask(Map<String, Object> resultMap) {
        Map lastRunningProgressTask =
                mongoTemplate.findOne(
                        Query.query(
                                Criteria.where("class_name")
                                        .is("ProgressTask")
                                        .and("status")
                                        .is(TaskStatusEnum.UPDATE.value))
                                .with(Sort.by("start_time").descending()),
                        Map.class,
                        "tracer");
        Map lastFinishedProgressTask =
                mongoTemplate.findOne(
                        Query.query(
                                Criteria.where("class_name")
                                        .is("ProgressTask")
                                        .and("status")
                                        .is(TaskStatusEnum.FINISHED.value))
                                .with(Sort.by("start_time").descending()),
                        Map.class,
                        "tracer");
        resultMap.put("lastRunningProgressTask", lastRunningProgressTask);
        resultMap.put("lastFinishedProgressTask", lastFinishedProgressTask);
    }

    private void getWeeklyCheckIn(Map<String, Object> resultMap) {
        resultMap.put(
                "weeklyCheckIn",
                mongoTemplate
                        .aggregate(
                                Aggregation.newAggregation(
                                        Aggregation.match(Criteria.where("message").is("签到")),
                                        Aggregation.project()
                                                .andExpression("week(_id)")
                                                .as("week")
                                                .andExpression("year(_id)")
                                                .as("year"),
                                        Aggregation.group("year", "week").count().as("count"),
                                        Aggregation.sort(Sort.by("year").ascending().and(Sort.by("week").ascending())),
                                        Aggregation.skip(1L),
                                        Aggregation.limit(10)),
                                "user_record",
                                Map.class)
                        .getMappedResults());
    }

    private void getMonthlySignIn(Map<String, Object> resultMap) {
        resultMap.put(
                "monthlySignIn",
                mongoTemplate
                        .aggregate(
                                Aggregation.newAggregation(
                                        Aggregation.project()
                                                .andExpression("month(_id)")
                                                .as("month")
                                                .andExpression("year(_id)")
                                                .as("year"),
                                        Aggregation.group("year", "month").count().as("count"),
                                        Aggregation.sort(Sort.by("year").ascending().and(Sort.by("month").ascending())),
                                        Aggregation.skip(1L),
                                        Aggregation.limit(10)),
                                "user",
                                Map.class)
                        .getMappedResults());
    }

    private void getRecordCount(Map<String, Object> resultMap) {
        resultMap.put("recordCount", mongoTemplate.count(new Query(), "user_record"));
    }
}
