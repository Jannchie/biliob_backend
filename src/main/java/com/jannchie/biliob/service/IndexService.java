package com.jannchie.biliob.service;


import com.jannchie.biliob.model.JannchieIndex;
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
}
