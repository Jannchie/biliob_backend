package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author jannchie
 */
@Service
public interface TracerService {

    /**
     * It is the function to get authors' queue status.
     *
     * @return The authors' queue status.
     */
    ResponseEntity getAuthorQueueStatus();

    /**
     * It is the function to get videos' queue status.
     *
     * @return The videos' queue status.
     */
    ResponseEntity getVideoQueueStatus();

    /**
     * Get the slice of exists task of the system.
     *
     * <p>
     *
     * <p>It is able to get the status of Biliob scheduler and Biliob spider.
     *
     * @param page     The page number of the task slice.
     * @param pagesize The page size of the task slice.
     * @return the slice of exists task of the system.
     */
    ResponseEntity sliceExistsTask(Integer page, Integer pagesize);

    /**
     * Get the slice of progress task of the system.
     *
     * <p>
     *
     * <p>It is able to get the status of Biliob link generate task.
     *
     * @param page     The page number of the task slice.
     * @param pagesize The page size of the task slice.
     * @return the slice of exists task of the system.
     */
    ResponseEntity sliceProgressTask(Integer page, Integer pagesize);

    /**
     * Get the slice of progress task of the system.
     *
     * <p>
     *
     * <p>It is able to get the status of Biliob link generate task.
     *
     * @param page     The page number of the task slice.
     * @param pagesize The page size of the task slice.
     * @param type     The type of the task slice.
     * @return the slice of exists task of the system.
     */
    ResponseEntity sliceSpiderTask(Integer page, Integer pagesize, Integer type);

    /**
     * Get the data for the dashboard page.
     *
     * @return the slice of exists task of the system.
     */
    ResponseEntity getDashboardData();

    /**
     * Get latest progress task response.
     *
     * @return response entity of latest progress task.
     */
    ResponseEntity getLatestProgressTaskResponse();

    /**
     * Get latest spider task response.
     *
     * @return response entity of latest spider task.
     */
    ResponseEntity getLatestSpiderTaskResponse();

    /**
     * Get author history queue status
     *
     * @return
     */
    ResponseEntity getHistoryQueueStatus();

    /**
     * 根据日期聚合访问作者数据的次数。
     *
     * @param limit 天数限制
     * @return 作者访问次数列表。
     */
    ResponseEntity listAuthorVisitRecord(Integer limit);
}
