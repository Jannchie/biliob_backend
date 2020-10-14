package com.jannchie.biliob.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jannchie
 */
@Service
public interface SiteService {

    /**
     * Get the data of the number of people watching video on bilibili.
     *
     * @param days The days of data that this method should return.
     * @return Online number.
     */
    ResponseEntity listOnline(Integer days);

    /**
     * Get the data of the number of video and author being observed. Get number of observers.
     *
     * @return The data of the number of video and author being observed and get the number of
     * observers.
     */
    Map getBiliOBCounter();

    /**
     * Get the number of author be observed.
     *
     * @return the number of author be observed.
     */
    Map getAuthorCount();

    /**
     * Get the number of video be observed.
     *
     * @return the number of video be observed.
     */
    Map getVideoCount();

    /**
     * Get the number of user be observed.
     *
     * @return the number of user be observed.
     */
    Map getUserCount();

    /**
     * Get site alert
     *
     * @return Site alert
     */
    Map getAlert();

    /**
     * Set site alert
     *
     * @return result
     */
    ResponseEntity postAlert();

    List<?> listSponsor(Integer page, Long pageSize, Integer sort);
}
