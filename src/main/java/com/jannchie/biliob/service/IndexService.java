package com.jannchie.biliob.service;


import com.jannchie.biliob.model.JannchieIndex;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Jannchie
 */
@Service
public interface IndexService {
    /**
     * @param keyword
     * @return
     */
    JannchieIndex getIndex(String keyword);

    JannchieIndex getSimIndex(String keyword);

    @Cacheable(key = "#keyword")
    public JannchieIndex getJannchieIndex(String keyword);
}
