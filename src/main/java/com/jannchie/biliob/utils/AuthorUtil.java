package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.AuthorRankData;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void getInterval(List<Author> authors) {
        List<Long> midList = authors.stream().map(Author::getMid).collect(Collectors.toList());
        Query q = Query.query(Criteria.where("mid").in(midList));
        Map<Long, Integer> intervalMap = mongoTemplate.find(q, AuthorIntervalRecord.class)
                .stream().collect(Collectors.toMap(AuthorIntervalRecord::getMid, AuthorIntervalRecord::getInterval));
        for (Author author : authors
        ) {
            author.setObInterval(intervalMap.get(author.getMid()));
        }
    }

    public void getInterval(Author author) {
        AuthorIntervalRecord authorIntervalRecord = mongoTemplate.findOne(Query.query(Criteria.where("mid").is(author.getMid())), AuthorIntervalRecord.class);
        author.setObInterval(authorIntervalRecord != null ? authorIntervalRecord.getInterval() : -1);
    }
}
