package com.jannchie.word.utils;

import com.jannchie.word.model.ReciteRecord;
import com.jannchie.word.model.User;
import com.jannchie.word.model.WordList;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jannchie
 */
@Component
public class UserUtils {
    private static MongoTemplate mongoTemplate;
    @Autowired
    public UserUtils(MongoTemplate mongoTemplate) {
        UserUtils.mongoTemplate = mongoTemplate;
    }
    public static User getUserByUsername(String username)  {
        User user = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup("wordList","myWordList","_id","myWordList"),
                // 排除 myWordList.wordList
                Aggregation.project().andExpression("{ wordList: 0 }").as("myWordList")
        ) ,User.class, User.class).getUniqueMappedResult();
        if (user == null) {
            return null;
        }
        HashMap<?,?> hashMap = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("username").is(user.getUsername())),
                        Aggregation.group("username").sum("skillExp").as("exp"),
                        Aggregation.project("exp")
                ),ReciteRecord.class,HashMap.class
        ).getUniqueMappedResult();
        if (hashMap != null) {
            user.setExp((Integer) hashMap.get("exp"));
        }
        setUserWordListInfo(user.getUsername(),user.getMyWordList());
        return user;
    }
    public static User getUser()  {
        String username = getUsername();
        return getUserByUsername(username);
    }

    public static String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public static void setUserWordListInfo(String username, List<WordList> result) {
        for (WordList e : result) {
            getWordListData(username, e);
        }
    }
    public static WordList getWordListDataByLid(String username, ObjectId lid) {
        Query q = Query.query(Criteria.where("_id").is(lid));
        q.fields().exclude("wordList");
        WordList wl = mongoTemplate.findOne(q,WordList.class);
        if (wl != null) {
            getWordListData( username, wl);
        }
        return wl;
    }
    private static void getWordListData(String username, WordList e) {
        Query q = Query.query(Criteria.where("_id").is(e.getId()));
        WordList w = mongoTemplate.findOne(q, WordList.class);
        if (w != null) {
            List<ReciteRecord> rrl = mongoTemplate.find(Query.query(Criteria.where("username").is(username).and("wordId").in(w.getWordList())), ReciteRecord.class);
            int mastered = 0;
            int reciting = 0;

            Integer size = w.getWordList().size();
            for (ReciteRecord rr : rrl
            ) {
                if (rr.getSkillExp() >= 100) {
                    mastered++;
                } else {
                    reciting++;
                }
            }
            if (e.getInfo() == null) {
                mongoTemplate.updateFirst(q, Update.update("info", new WordList.Info(size)), WordList.class);
                e.setInfo(new WordList.Info(size,mastered,reciting));
            }else{
                e.setInfo(new WordList.Info(w.getInfo().getCount(),mastered,reciting));
            }
        }
    }

}
