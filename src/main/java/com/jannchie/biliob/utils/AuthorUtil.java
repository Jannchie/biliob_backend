package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.AuthorRankData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author Pan Jianqi
 */
@Component
public class AuthorUtil {
    private MongoTemplate mongoTemplate;

    @Autowired
    public AuthorUtil(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AuthorRankData getRankData(Author author) {
        Long archiveViewRank = null != author.getcArchiveView() && author.getcArchiveView() != 0 ? mongoTemplate.count(Query.query(Criteria.where("cArchive_view").gte(author.getcArchiveView())), "author") : -1;
        Long articleViewRank = null != author.getcArticleView() && author.getcArticleView() != 0 ? mongoTemplate.count(Query.query(Criteria.where("cArticle_view").gte(author.getcArticleView())), "author") : -1;
        Long likeRank = null != author.getcLike() && author.getcLike() != 0 ? mongoTemplate.count(Query.query(Criteria.where("cLike").gte(author.getcLike())), "author") : -1;
        Long fansRank = null != author.getcFans() && author.getcFans() != 0 ? mongoTemplate.count(Query.query(Criteria.where("cFans").gte(author.getcFans())), "author") : -1;
        return new AuthorRankData(archiveViewRank, articleViewRank, likeRank, fansRank);
    }

    @Cacheable(value = "author_rank", key = "#author.getMid()")
    public AuthorRankData getLastRankData(Author author) {
        return getRankData(author);
    }

}
