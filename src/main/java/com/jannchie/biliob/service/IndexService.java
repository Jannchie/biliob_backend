package com.jannchie.biliob.service;


import com.jannchie.biliob.model.JannchieIndex;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jannchie
 */
@Service
public interface IndexService {
    /**
     * Get Index
     *
     * @param keyword Key word
     * @return index
     */
    JannchieIndex getIndex(String keyword);

    /**
     * Get Sim Index
     *
     * @param keyword keyword
     * @return sim index
     */
    JannchieIndex getSimIndex(String keyword);

    /**
     * Get Cached Index
     *
     * @param keyword keyword
     * @return index
     */
    public JannchieIndex getJannchieIndex(String keyword);

    /**
     * Get Recently Rank
     *
     * @return Recently Rank
     */
    List<?> getRecentlyRank();
}
