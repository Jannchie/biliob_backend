package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jannchie
 */
@Service
public interface AuthorAchievementService {

    /**
     * analyze author achievement
     *
     * @param mid author id
     * @return result
     */
    Result<?> analyzeAuthorAchievement(Long mid);


    /**
     * rapidly analyze author achievement
     *
     * @param author author
     */
    void rapidlyAnalyzeAuthorAchievement(Author author);

    /**
     * analyze all author achievement
     *
     * @return result
     */
    Result<?> analyzeAllAuthorAchievement();

    /**
     * Analyze daily achievement
     *
     * @param mid mid
     * @return analyze result
     */
    Result<?> analyzeDailyAchievement(Long mid);

    /**
     * @param level level
     * @return result
     */
    List<Author.Achievement> getAuthorAchievementByLevel(Integer level);
}
