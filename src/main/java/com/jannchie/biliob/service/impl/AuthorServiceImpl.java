package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.AuthorSortEnum;
import com.jannchie.biliob.constant.BiliobConstant;
import com.jannchie.biliob.constant.PageSizeEnum;
import com.jannchie.biliob.exception.AuthorAlreadyFocusedException;
import com.jannchie.biliob.model.Author;
import com.jannchie.biliob.model.AuthorRankData;
import com.jannchie.biliob.model.RealTimeFans;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.object.AuthorIntervalRecord;
import com.jannchie.biliob.object.AuthorVisitRecord;
import com.jannchie.biliob.repository.AuthorRepository;
import com.jannchie.biliob.repository.RealTimeFansRepository;
import com.jannchie.biliob.service.AdminService;
import com.jannchie.biliob.service.AuthorAchievementService;
import com.jannchie.biliob.service.AuthorService;
import com.jannchie.biliob.utils.*;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Projections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.jannchie.biliob.constant.TimeConstant.SECOND_OF_DAY;
import static com.jannchie.biliob.constant.TimeConstant.SECOND_OF_MINUTES;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Service
@CacheConfig(cacheNames = "authorService")
public class AuthorServiceImpl implements AuthorService {
    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);
    private final RedisOps redisOps;
    private final AuthorRepository respository;
    private final RealTimeFansRepository realTimeFansRepository;
    private final MongoTemplate mongoTemplate;
    private MongoClient mongoClient;
    private AuthorUtil authorUtil;
    private BiliobUtils biliOBUtils;
    private AdminService adminService;


    private AuthorAchievementService authorAchievementService;

    @Autowired
    public AuthorServiceImpl(AuthorRepository respository,
                             MongoClient mongoClient, MongoTemplate mongoTemplate, InputInspection inputInspection,
                             AuthorUtil authorUtil, RealTimeFansRepository realTimeFansRepository,
                             RedisOps redisOps, BiliobUtils biliOBUtils, AdminService adminService, AuthorAchievementService authorAchievementService) {
        this.respository = respository;
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
        this.authorUtil = authorUtil;
        this.realTimeFansRepository = realTimeFansRepository;
        this.redisOps = redisOps;
        this.biliOBUtils = biliOBUtils;
        this.adminService = adminService;
        this.authorAchievementService = authorAchievementService;
    }

    private MatchOperation getAggregateMatch(int days, Long mid) {
        if (days == -1) {
            return Aggregation.match(Criteria.where("mid").is(mid));
        } else {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -days);
            return Aggregation.match(Criteria.where("mid").is(mid).and("datetime").gt(c.getTime()));
        }
    }

    private Author getAggregatedData(Long mid, int days) {
        Calendar timer = Calendar.getInstance();
        MatchOperation match = getAggregateMatch(days, mid);
        Aggregation a = Aggregation.newAggregation(
                match,
                Aggregation.project("fans", "archiveView", "articleView", "like", "attention", "datetime", "mid").and("datetime").dateAsFormattedString("%Y-%m-%d").as("date"),
                Aggregation.group("date")
                        .first("datetime").as("datetime")
                        .first("fans").as("fans")
                        .first("archiveView").as("archiveView")
                        .first("articleView").as("articleView")
                        .first("like").as("like")
                        .first("attention").as("attention")
                        .first("mid").as("mid"),
                Aggregation.sort(Sort.Direction.DESC, "datetime"),
                Aggregation.group().push(
                        new BasicDBObject("datetime", "$datetime")
                                .append("fans", "$fans")
                                .append("archiveView", "$archiveView")
                                .append("articleView", "$articleView")
                                .append("archive", "$archive")
                                .append("article", "$article")
                                .append("like", "$like")
                ).as("data").first("mid").as("mid"),
                Aggregation.lookup("author", "mid", "mid", "author"),
                Aggregation.unwind("author"),
                (aoc) -> new Document("$addFields", new Document("author.data", "$data")),
                Aggregation.replaceRoot("author"),
                Aggregation.lookup("author_interval", "mid", "mid", "interval"),
                (aoc) -> new Document("$addFields", new Document("obInterval", new Document("$arrayElemAt", Arrays.asList("$interval.interval", 0)))),
                Aggregation.lookup("author_achievement", "mid", "author.mid", "achievements"),
                Aggregation.project().andExpression("{ mid: 0}").as("data")
        );
        Author data = mongoTemplate.aggregate(a, Author.Data.class, Author.class).getUniqueMappedResult();
        long deltaTime = Calendar.getInstance().getTimeInMillis() - timer.getTimeInMillis();
        // 太慢，则精简数据
        logger.info(deltaTime);
        if (deltaTime > 7000) {
            adminService.reduceByMid(mid);
        }
        return data;
    }

    @Override
    public Author getAggregatedData(Long mid) {
        return getAggregatedData(mid, -1);
    }

    private void setFreq(Long mid) {
        if (!mongoTemplate.exists(Query.query(Criteria.where("mid").is(mid)), AuthorIntervalRecord.class)) {
            this.upsertAuthorFreq(mid, SECOND_OF_DAY);
        }
    }

    private void setFreq(Author author) {
        setFreq(author.getMid());
    }

    @Override
    public void getRankData(Author author) {
        if (author == null || author.getMid() == null) {
            return;
        }
        AuthorRankData lastRankData = authorUtil.getLastRankData(author);
        AuthorRankData currentRankData = getCurrentRankData(author);
        Date date = Calendar.getInstance().getTime();
        if (author.getData() != null) {
            date = author.getData().get(0).getDatetime();
        }
        Author.Rank rank = new Author.Rank(currentRankData.getFansRank(),
                currentRankData.getArchiveViewRank(), currentRankData.getArticleViewRank(),
                currentRankData.getLikeRank(),
                getDelta(currentRankData.getFansRank(), lastRankData.getFansRank()),
                getDelta(currentRankData.getArchiveViewRank(), lastRankData.getArchiveViewRank()),
                getDelta(currentRankData.getArticleViewRank(), lastRankData.getArticleViewRank()),
                getDelta(currentRankData.getLikeRank(), lastRankData.getLikeRank()), date);
        author.setRank(rank);
    }

    private Long getDelta(Long a, Long b) {
        if (a == -1 || b == -1) {
            return 0L;
        } else {
            return a - b;
        }
    }


    public AuthorRankData getCurrentRankData(Author author) {
        return authorUtil.getRankData(author);
    }


    @Override
    public Author getAuthorDetails(Long mid, int days) {
        addAuthorVisit(mid);
        Author author = getAggregatedData(mid, days);
        if (author == null) {
            return null;
        }
        disposeAuthor(author);
        return author;
    }

    public Integer getTopFansRateAuthorFans() {
        Query q = new Query().with(Sort.by(Sort.Direction.DESC, "cRate"));
        q.fields().include("cFans");
        return Objects.requireNonNull(mongoTemplate.findOne(q, Author.class)).getcFans();
    }

    @Override
    public List<Author> getHomePageCompareAuthors() {
        Integer fans = getTopFansRateAuthorFans();
        Query q = Query.query(Criteria.where("cFans").gte(fans)).with(Sort.by(Sort.Direction.ASC, "cFans")).limit(2);
        q.fields().exclude("data").exclude("keyword");
        List<Author> authors = mongoTemplate.find(q, Author.class).stream().sorted(Comparator.comparing(Author::getcFans).reversed()).collect(Collectors.toList());
        if (authors.size() == 1) {
            q = new Query().with(Sort.by("cRate").descending()).limit(2);
            q.fields().exclude("data").exclude("keyword");
            authors = mongoTemplate.find(q, Author.class);
        }
        return authors;
    }

    @Override
    public List<Author.Data> getHistoryData(Long mid) {
        return mongoTemplate.find(Query.query(Criteria.where("mid").is(mid)).limit(100), Author.Data.class);
    }


    @Override
    public Author getAuthorDetails(Long mid) {
        addAuthorVisit(mid);
        Author author = getAggregatedData(mid);
        if (author == null) {
            return null;
        }
        disposeAuthor(author);
        return author;
    }

    public void disposeAuthor(Author author) {
        getRankData(author);
        mongoTemplate.updateFirst(Query.query(Criteria.where("mid").is(author.getMid())), Update.update("rank", author.getRank()), Author.class);
        if (author.getAchievements() != null) {
            authorAchievementService.rapidlyAnalyzeAuthorAchievement(author);
            authorAchievementService.analyzeDailyAchievement(author.getMid());
        }
        if (author.getData() != null) {
            filterAuthorData(author);
        }
    }

    private void filterAuthorData(Author author, Integer days) {
        ArrayList<Author.Data> tempData = author.getData();
        tempData.removeIf(data -> {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, -days);
                    return data.getDatetime().before(c.getTime());
                }
        );
        author.setData(tempData);
    }

    private void filterAuthorData(Author author) {
        User user = UserUtils.getUser();
        if (user == null || user.getExp() < 100) {
            ArrayList<Author.Data> tempData = author.getData();
            tempData.removeIf(data -> {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DATE, -BiliobConstant.GUEST_VIEW_MAX_DAYS);
                        return data.getDatetime().before(c.getTime());
                    }
            );
            author.setData(tempData);
        }
    }

    private void addAuthorVisit(Long mid) {
        String finalUserName = biliOBUtils.getUserName();
        Map data = biliOBUtils.getVisitData(finalUserName, mid);
        if (mongoTemplate.exists(Query.query(Criteria.where("name").is(finalUserName)), "blacklist_user")) {
            adminService.banItself("用户被禁用", false);
        }
        if (mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(where("name").is(finalUserName)),
                Aggregation.group("user-agent")
        ), "author_visit", Map.class).getMappedResults().size() > 16) {
            adminService.banItself("设备异常多", false);
        }
        AuthorServiceImpl.logger.info("用户[{}]查询mid[{}]的详细数据", finalUserName, mid);
        mongoTemplate.insert(data, "author_visit");
    }

    @Override
    public void postAuthorByMid(Long mid)
            throws AuthorAlreadyFocusedException {
        if (respository.findByMid(mid) != null) {
            throw new AuthorAlreadyFocusedException(mid);
        }
        AuthorServiceImpl.logger.info(mid);
        upsertAuthorFreq(mid, SECOND_OF_DAY);
        respository.save(new Author(mid));
    }

    @Override
    @Cacheable(value = "author_slice", key = "#mid + #text + #page + #pagesize + #sort")
    public MySlice<Author> getAuthor(Long mid, String text, Integer page, Integer pagesize,
                                     Integer sort) {
        if (pagesize > PageSizeEnum.BIG_SIZE.getValue()) {
            pagesize = PageSizeEnum.BIG_SIZE.getValue();
        }
        MySlice<Author> result;
        String sortKey = AuthorSortEnum.getKeyByFlag(sort);
        if (mid != -1) {
            AuthorServiceImpl.logger.info(mid);
            result = new MySlice<>(respository.searchByMid(mid,
                    PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
        } else if (!Objects.equals(text, "")) {
            AuthorServiceImpl.logger.info(text);
            if (InputInspection.isId(text)) {
                // get a mid
                result = new MySlice<>(respository.searchByMid(Long.valueOf(text),
                        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
            } else {

                // get text
                String[] textArray = text.split(" ");
                result = new MySlice<>(respository.findByKeywordContaining(textArray,
                        PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
                if (result.getContent().isEmpty()) {
                    for (String eachText : textArray) {
                        HashMap<String, String> map = new HashMap<>(1);
                        map.put("mid", eachText);
                        mongoTemplate.insert(map, "search_word");
                    }
                }
            }

        } else {
            AuthorServiceImpl.logger.info("查看所有UP主列表");
            result = new MySlice<>(respository.listAll(
                    PageRequest.of(page, pagesize, new Sort(Sort.Direction.DESC, sortKey))));
        }

        authorUtil.getInterval(result.getContent());
        return result;
    }

    /**
     * get a list of author's fans increase rate.
     *
     * @return list of author rate of fans increase.
     */
    @Override
    public ResponseEntity listFansIncreaseRate() {
        Slice<Author> slice = respository
                .listTopIncreaseRate(PageRequest.of(0, 20, new Sort(Sort.Direction.DESC, "cRate")));
        AuthorServiceImpl.logger.info("获得涨粉榜");
        return new ResponseEntity<>(slice, HttpStatus.OK);
    }

    /**
     * get a list of author's fans decrease rate.
     *
     * @return list of author rate of fans decrease.
     */
    @Override
    public ResponseEntity listFansDecreaseRate() {
        Slice<Author> slice = respository
                .listTopIncreaseRate(PageRequest.of(0, 20, new Sort(Sort.Direction.ASC, "cRate")));
        AuthorServiceImpl.logger.info("获得掉粉榜");
        return new ResponseEntity<>(slice, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getTopAuthor() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("CTT"));
        c.add(Calendar.HOUR, 7);
        Date cDate = c.getTime();
        AggregateIterable<Document> r = mongoClient.getDatabase("biliob").getCollection("author")
                .aggregate(Arrays.asList(sort(descending("cFans")), limit(2),
                        project(Projections.fields(Projections.excludeId(),
                                Projections.include("name", "face", "official"),
                                Projections.computed("data",
                                        new Document().append("$filter",
                                                new Document().append("input", "$data")
                                                        .append("as", "eachData")
                                                        .append("cond", new Document().append("$gt",
                                                                Arrays.asList("$$eachData.datetime",
                                                                        cDate)))))))));
        ArrayList<Document> result = new ArrayList<>(2);
        for (Document document : r) {
            result.add(document);
        }

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity getLatestTopAuthorData() {
        AggregateIterable<Document> r = mongoClient.getDatabase("biliob").getCollection("author")
                .aggregate(Arrays.asList(sort(descending("cFans")), limit(2),
                        project(Projections.fields(Projections.excludeId(),
                                Projections.include("name", "face", "official"),
                                Projections.computed("data",
                                        new Document("$slice", Arrays.asList("$data", 1)))))));
        ArrayList<Document> result = new ArrayList<>(2);
        for (Document document : r) {
            result.add(document);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * get author information exclude history data.
     *
     * @param mid author id
     * @return author
     */
    @Override
    public Author getAuthorInfo(Long mid) {
        Author author = respository.findAuthorByMid(mid);
        disposeAuthor(author);
        return author;
    }


    /**
     * list real time data
     *
     * @param aMid one author id
     * @param bMid another author id
     * @return Real time fans responseEntity
     */
    @Override
    public ResponseEntity getRealTimeData(Long aMid, Long bMid) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+:00:00"));

        List<RealTimeFans> aRealTimeFans = listRealTimeFans(aMid);
        ArrayList<Integer> aFans = new ArrayList<>();
        ArrayList<String> datetime = new ArrayList<>();
        for (RealTimeFans item : aRealTimeFans) {
            c.setTime(item.getDatetime());
            datetime.add(format.format(c.getTime()));
            aFans.add(item.getFans());
        }

        List<RealTimeFans> bRealTimeFans = listRealTimeFans(bMid);
        ArrayList<Integer> bFans = new ArrayList<>();
        for (RealTimeFans item : bRealTimeFans) {
            bFans.add(item.getFans());
        }

        HashMap<String, Cloneable> result = new HashMap<>(3);
        result.put("aFans", aFans);
        result.put("bFans", bFans);
        result.put("datetime", datetime);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private List<RealTimeFans> listRealTimeFans(Long mid) {
        return realTimeFansRepository.findTop180ByMidOrderByDatetimeDesc(mid);
    }

    /**
     * list author tag
     *
     * @param mid author id
     * @return tag list
     */
    @Override
    public List<Map> listAuthorTag(Long mid, Integer limit) {
        Aggregation a = Aggregation.newAggregation(Aggregation.match(Criteria.where("mid").is(mid)),
                Aggregation.unwind("tag"), Aggregation.project("tag", "cView"),
                Aggregation.group("tag").sum("cView").as("totalView").count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"), Aggregation.limit(limit));
        return mongoTemplate.aggregate(a, "video", Map.class).getMappedResults();
    }

    /**
     * list relate author by author id
     *
     * @param mid   author id
     * @param limit length of result list
     * @return author list
     */
    @Override
    @Cacheable(value = "relate_author", key = "#mid + #limit")
    public List listRelatedAuthorByMid(Long mid, Integer limit) {
        int tagLimit = limit;
        List<Map> tagMap = listAuthorTag(mid, 5);
        List cList = new ArrayList<>();
        for (Map item : tagMap) {
            cList.add(item.get("_id"));
        }
        if (cList.size() == 0) {
            return cList;
        }
        List<Map> result = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -3);

        Aggregation a = Aggregation.newAggregation(Aggregation.match(Criteria.where("mid").is(mid)),
                Aggregation.lookup("author", "mid", "mid", "authorDoc"), Aggregation.unwind("tag"),
                Aggregation.group("mid").count().as("count").avg("cView").as("value").last("author")
                        .as("name").addToSet("tag").as("tag").last("authorDoc.face").as("face"),
                Aggregation.unwind("face"), Aggregation.sort(Sort.Direction.DESC, "value"));
        Map host = mongoTemplate.aggregate(a, "video", Map.class).getUniqueMappedResult();
        while (result.size() <= 6) {
            List hostTag = (List) (host != null ? host.get("tag") : null);

            Aggregation b = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("datetime").gt(c.getTime()).and("tag")
                            .all(cList).and("mid").ne(mid)),
                    Aggregation.limit(5000),
                    Aggregation.lookup("author", "mid", "mid", "authorDoc"),
                    Aggregation.unwind("tag"),
                    Aggregation.group("mid").count().as("count").avg("cView").as("value")
                            .last("author").as("name").addToSet("tag").as("tag")
                            .last("authorDoc.face").as("face"),
                    Aggregation.unwind("face"), Aggregation.sort(Sort.Direction.DESC, "value"),
                    Aggregation.limit(20));

            for (Map item : mongoTemplate.aggregate(b, "video", Map.class).getMappedResults()) {
                Boolean flag = false;
                for (Map resultItem : result) {
                    if (item.get("_id").equals(resultItem.get("_id"))) {
                        flag = true;
                    }
                }
                if (!flag) {
                    List<String> tempTagList = new ArrayList<>();
                    for (String tag : (List<String>) item.get("tag")) {
                        if (hostTag != null && hostTag.contains(tag)) {
                            tempTagList.add(tag);
                        }
                    }
                    item.put("tag", tempTagList);
                    result.add(item);
                }
            }
            if (cList.size() <= 1) {
                break;
            }
            cList.remove(cList.size() - 1);
        }
        result.add(host);
        return result;
    }

    @Override
    public void upsertAuthorFreq(Long mid, Integer interval) {
        upsertAuthorFreq(mid, interval, interval);
    }


    @Override
    public void upsertAuthorFreq(Long mid, Integer interval, Integer delay) {
        AuthorIntervalRecord preInterval =
                mongoTemplate.findOne(Query.query(Criteria.where("mid").is(mid)),
                        AuthorIntervalRecord.class, "author_interval");
        Calendar nextCal = Calendar.getInstance();
        Date cTime = Calendar.getInstance().getTime();
        nextCal.add(Calendar.SECOND, delay);
        // 更新访问频率数据。
        Update u = Update.update("date", cTime);
        if (preInterval == null || preInterval.getInterval() == null || interval < preInterval.getInterval()) {
            u.set("interval", interval);
        }
        // 如果此前没有访问频率数据，或者更新后的访问时间比原来的访问时间还短，则刷新下次访问的时间。
        if (preInterval == null || preInterval.getNext() == null) {
            u.set("next", cTime);
            logger.info("[UPSERT] 作者：{} 访问频率：{} 下次爬取：{}", mid, interval, cTime);
        } else if (nextCal.getTimeInMillis() < preInterval.getNext().getTime()) {
            u.set("next", nextCal.getTime());
            logger.info("[UPSERT] 作者：{} 访问频率：{} 下次爬取：{}", mid, interval, nextCal.getTime());
        }
        mongoTemplate.upsert(Query.query(Criteria.where("mid").is(mid)), u, AuthorIntervalRecord.class);
    }

    @Override
    public List<AuthorVisitRecord> listMostVisitAuthorId(Integer days, Integer limit) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -days);
        List<AuthorVisitRecord> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(Aggregation.match(where("date").gt(c.getTime())),
                        Aggregation.group("mid").count().as("count").first("mid").as("mid"),
                        Aggregation.sort(Sort.Direction.DESC, "count"), Aggregation.limit(limit)),
                "author_visit", AuthorVisitRecord.class).getMappedResults();
        Query q = Query.query(Criteria.where("mid")
                .in(results.stream().map(AuthorVisitRecord::getMid).collect(Collectors.toList())));
        q.fields().include("name").include("mid");
        List<Author> authorList = mongoTemplate.find(q, Author.class);
        for (AuthorVisitRecord result : results) {
            for (Author author : authorList) {
                if (result.getMid().equals(author.getMid())) {
                    result.setName(author.getName());
                }
            }

        }
        return results;
    }

    public List<Map> listMostVisitAuthorId(Integer days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return mongoTemplate
                .aggregate(
                        Aggregation.newAggregation(Aggregation.match(Criteria.where("date").gt(c)),
                                Aggregation.group("mid").count().as("count"),
                                Aggregation.sort(Sort.Direction.DESC, "count")),
                        "author_visit", Map.class)
                .getMappedResults();
    }


    @Override
    public void updateObserveFreqPerMinute() {
        HashMap<Long, Integer> intervalMap = new HashMap<>();
        calculateTopVisitIntervalData(intervalMap);
        calculateHomePageAuthor(intervalMap);
        calculateTopClassIntervalData(intervalMap);
        calculateFansRankIntervalData(intervalMap);
        logger.fatal("[START] 调整观测频率: 本次计划调整 {} 个UP主的频率", intervalMap.size());
        intervalMap.forEach(this::upsertAuthorFreq);
        logger.fatal("[FINISH] 调整观测频率 完成");
    }

    private void calculateHomePageAuthor(HashMap<Long, Integer> intervalMap) {
        List<Author> authors = this.getHomePageCompareAuthors();
        List<AuthorIntervalRecord> authorIntervalRecords = mongoTemplate.find(Query.query(Criteria.where("interval").is(SECOND_OF_MINUTES)), AuthorIntervalRecord.class);
        for (AuthorIntervalRecord authorIntervalRecord : authorIntervalRecords
        ) {
            setIntervalMap(intervalMap, authorIntervalRecord.getMid(), SECOND_OF_MINUTES * 60);
        }
        for (Author author : authors
        ) {
            setIntervalMap(intervalMap, author.getMid(), SECOND_OF_MINUTES);
        }
    }

    public void calculateTopVisitIntervalData(HashMap<Long, Integer> intervalMap) {
        // 点击频率最高，每十分钟一次
        List<AuthorVisitRecord> authorList = this.listMostVisitAuthorId(1, 30);
        for (AuthorVisitRecord author : authorList) {
            setIntervalMap(intervalMap, author.getMid(), SECOND_OF_MINUTES * 60);
        }
    }

    public void calculateTopClassIntervalData(HashMap<Long, Integer> intervalMap) {
        // 各指标最高，前三名：每15分钟一次；前20名：每360分钟一次。
        for (int i = 0; i <= 3; i++) {
            List<Author> authors = mongoTemplate.aggregate(
                    Aggregation.newAggregation(
                            Aggregation.sort(Sort.Direction.DESC, AuthorSortEnum.getKeyByFlag(i)),
                            Aggregation.limit(20),
                            Aggregation.project("mid")),
                    Author.class, Author.class).getMappedResults();
            int idx = 0;
            for (Author author : authors) {
                setIntervalMap(intervalMap, author.getMid(), (idx++ <= 3) ? SECOND_OF_MINUTES * 15 : SECOND_OF_MINUTES * 60 * 6);
            }
        }
    }

    public void calculateFansRankIntervalData(HashMap<Long, Integer> intervalMap) {
        // 涨掉粉榜，前三名：每5分钟一次；前20名：每60分钟一次。
        Sort.Direction[] d = {Sort.Direction.DESC, Sort.Direction.ASC};
        for (Sort.Direction direction : d) {
            Query q = new Query(Criteria.where("cRate").exists(true)).with(Sort.by(direction, "cRate"));
            q.fields().include("mid");
            List<Author> authors = mongoTemplate.find(q.limit(20), Author.class);
            int idx = 0;
            for (Author author : authors) {
                setIntervalMap(intervalMap, author.getMid(), (idx++ <= 3) ? SECOND_OF_MINUTES * 5 : SECOND_OF_MINUTES * 60);
            }
        }
    }

    private void setIntervalMap(HashMap<Long, Integer> intervalMap, Long mid, Integer interval) {
        if (intervalMap.get(mid) == null || intervalMap.get(mid) > interval) {
            intervalMap.put(mid, interval);
        }
    }


    @Override
    public void updateObserveFreq() {


        HashMap<Long, Integer> intervalMap = new HashMap<>();
        // 1万粉丝以上：正常观测
        List<Author> authorList = getAuthorFansGt(10000);
        for (Author author : authorList) {
            setIntervalMap(intervalMap, author.getMid(), SECOND_OF_DAY);
        }

        // 人为设置：强行观测
        Query query = Query.query(Criteria.where("forceFocus").is(true));
        query.fields().include("mid");
        List<Author> forceFocusAuthors = mongoTemplate.find(query, Author.class);
        for (Author author : forceFocusAuthors) {
            setIntervalMap(intervalMap, author.getMid(), SECOND_OF_MINUTES * 60 * 12);
        }

        // 百万粉以上：高频观测
        authorList = this.getAuthorFansGt(1000000);
        for (Author author : authorList) {
            setIntervalMap(intervalMap, author.getMid(), SECOND_OF_MINUTES * 60 * 12);
        }

        logger.fatal("[START] 调整观测频率: 本次计划调整 {} 个UP主的频率", intervalMap.size());
        intervalMap.forEach(this::upsertAuthorFreq);
        logger.fatal("[FINISH] 调整观测频率 完成");

    }

    @Override
    public List<Author> getAuthorFansGt(int gt) {
        Query q = Query.query(Criteria.where("cFans").gt(gt));
        q.fields().include("mid");
        return mongoTemplate.find(q, Author.class, "author");
    }


    @Override
    public List<AuthorVisitRecord> listHotAuthor() {
        return this.listMostVisitAuthorId(1, 10);
    }

    @Override
    public List<Long> getTopFansAuthors(int limit) {
        Query q = new Query().with(Sort.by("cFans").descending()).limit(limit);
        q.fields().include("mid");
        return mongoTemplate.find(q, Author.class).stream().map(Author::getMid).collect(Collectors.toList());
    }
}
