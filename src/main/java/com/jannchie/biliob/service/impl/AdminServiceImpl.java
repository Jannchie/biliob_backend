package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.ScheduleItem;
import com.jannchie.biliob.model.SearchMethod;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.service.AdminService;
import com.jannchie.biliob.utils.LoginChecker;
import com.jannchie.biliob.utils.Result;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.BucketAutoOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author jannchie
 */
@Service
public class AdminServiceImpl implements AdminService {
    final UserRepository userRepository;
    final MongoTemplate mongoTemplate;
    @Autowired
    final MongoClient mongoClient;

    @Autowired
    public AdminServiceImpl(
            UserRepository userRepository, MongoTemplate mongoTemplate, MongoClient mongoClient) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
    }

    /**
     * list User
     *
     * @param page     page
     * @param pagesize pageszie
     * @param sort     sort
     * @param text     text
     * @param day      @return user list
     */
    @Override
    public List listUser(Integer page, Integer pagesize, Integer sort, String text, Integer day) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -day);
        ArrayList<AggregationOperation> aggregationList = new ArrayList<>();
        if (text != null) {
            aggregationList.add(Aggregation.match(Criteria.where("name").is(text)));
        }
        aggregationList.add(Aggregation.match(Criteria.where("datetime").gt(c.getTime())));
        aggregationList.add(Aggregation.limit(pagesize));
        aggregationList.add(Aggregation.skip((long) page));
        Aggregation a = Aggregation.newAggregation(aggregationList);

        return mongoTemplate.aggregate(a, "user", Map.class).getMappedResults();
    }

    @Override
    public List listIpRecord(Integer page, Integer pagesize, String groupBy, String text, Integer day) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -day);
        ArrayList<AggregationOperation> aggregationList = new ArrayList<>();
        if (!text.equals("")) {
            aggregationList.add(Aggregation.match(Criteria.where("name").is(text)));
        }
        aggregationList.add(Aggregation.group("ip").count().as("count").first("datetime").as("firstTime").last("datetime").as("lastTime"));
        aggregationList.add(Aggregation.sort(Sort.Direction.DESC, "count"));
        aggregationList.add(Aggregation.limit(pagesize));
        aggregationList.add(Aggregation.skip((long) page));
        Aggregation a = Aggregation.newAggregation(aggregationList);

        return mongoTemplate.aggregate(a, "ip_visit_record", Map.class).getMappedResults();
    }

    /**
     * aggregate user
     *
     * @param ops operations
     * @return list of result
     */
    @Override
    public List aggregateUser(List<Map<String, Object>> ops) {
        String command = "db.user.aggregate([{$group:{_id:'$name',count:{$sum:'$EXP'}}}])";
        BasicDBObject bson = new BasicDBObject();
        bson.put("$eval", command);

        mongoTemplate.getDb().runCommand(bson);

        return null;
    }

    @Override
    public List aggregateUser(
            Integer page,
            Integer pagesize,
            Integer day,
            String matchField,
            String matchMethod,
            String matchValue,
            Integer sort,
            String orderBy,
            Integer bucketType,
            String bucket,
            String groupByField,
            String groupReference,
            String groupKeyword) {
        List<AggregationOperation> aggregationOperationsList = new ArrayList<>();
        aggregationOperationsList.add(
                Aggregation.project("name", "exp", "credit", "mail", "role")
                        .andExclude()
                        .andExpression("dayOfMonth(_id)")
                        .as("day")
                        .andExpression("month(_id)")
                        .as("month")
                        .andExpression("year(_id)")
                        .as("year"));
        aggregateMatch(matchField, matchMethod, matchValue, aggregationOperationsList);
        // aggregateDays(day, aggregationOperationsList);
        aggregateGroup(orderBy, groupByField, groupReference, groupKeyword, aggregationOperationsList);
        aggregateSort(sort, orderBy, aggregationOperationsList);
        aggregateBucket(bucket, groupByField, aggregationOperationsList);
        aggregatePage(page, pagesize, aggregationOperationsList);
        return mongoTemplate
                .aggregate(Aggregation.newAggregation(aggregationOperationsList), "user", Map.class)
                .getMappedResults();
    }

    private void aggregateMatch(
            String matchField,
            String matchMethod,
            String matchValue,
            List<AggregationOperation> aggregationOperationsList) {
        String regex = "^[-+]?[\\d]*$";
        Pattern pattern = Pattern.compile(regex);
        if (!"".equals(matchField)) {
            if (Objects.equals(matchMethod, "is")) {
                if (pattern.matcher(matchValue).matches()) {
                    aggregationOperationsList.add(
                            Aggregation.match(Criteria.where(matchField).is(Double.valueOf(matchValue))));
                }
                aggregationOperationsList.add(Aggregation.match(Criteria.where(matchField).is(matchValue)));
            } else if (Objects.equals(matchMethod, "gt")) {
                aggregationOperationsList.add(
                        Aggregation.match(Criteria.where(matchField).gt(Integer.valueOf(matchValue))));
            } else if (Objects.equals(matchMethod, "lt")) {
                aggregationOperationsList.add(
                        Aggregation.match(Criteria.where(matchField).lt(Integer.valueOf(matchValue))));
            }
        }
    }

    private void aggregatePage(
            long page, Integer pagesize, List<AggregationOperation> aggregationOperationsList) {
        aggregationOperationsList.add(Aggregation.limit(pagesize));
        aggregationOperationsList.add(Aggregation.skip(page * pagesize));
    }

    private void aggregateSort(
            Integer sort, String orderBy, List<AggregationOperation> aggregationOperationsList) {
        if (!"".equals(orderBy)) {
            if (sort.equals(-1) && !"".equals(orderBy)) {
                aggregationOperationsList.add(Aggregation.sort(Sort.Direction.DESC, orderBy));
            } else {
                aggregationOperationsList.add(Aggregation.sort(Sort.Direction.ASC, orderBy));
            }
        }
    }

    private void aggregateBucket(
            String bucket, String groupByField, List<AggregationOperation> aggregationOperationsList) {
        if (Objects.equals(bucket, "auto")) {
            aggregationOperationsList.add(Aggregation.bucketAuto(groupByField, 10));
        } else if (Objects.equals(bucket, "E6")) {
            aggregationOperationsList.add(
                    Aggregation.bucketAuto(groupByField, 10)
                            .withGranularity(BucketAutoOperation.Granularities.E6));
        } else if (Objects.equals(bucket, "SERIES_1_2_5")) {
            aggregationOperationsList.add(
                    Aggregation.bucketAuto(groupByField, 10)
                            .withGranularity(BucketAutoOperation.Granularities.SERIES_1_2_5));
        }
    }

    private void aggregateGroup(
            String orderBy,
            String groupByField,
            String groupReference,
            String groupKeyword,
            List<AggregationOperation> aggregationOperationsList) {
        if (!"".equals(groupByField) && !"".equals(groupReference) && !"".equals(groupKeyword)) {
            GroupOperation g = Aggregation.group(groupByField).count().as("count");
            if (!"".equals(orderBy)) {
                g = g.first(orderBy).as(orderBy);
            }
            if (Objects.equals(groupKeyword, "sum")) {
                g = g.sum(groupReference).as("sum").sum("credit").as("credit").sum("exp").as("exp");
            } else if (Objects.equals(groupKeyword, "avg")) {
                g = g.avg(groupReference).as("avg").avg("credit").as("credit").avg("exp").as("exp");
            }
            aggregationOperationsList.add(g);
        }
    }

    private void aggregateDays(Integer day, List<AggregationOperation> aggregationOperationsList) {
        if (day != 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -day);
            aggregationOperationsList.add(Aggregation.match(Criteria.where("datetime").gt(c.getTime())));
        }
    }

    @Override
    public ResponseEntity grantUserAdminRole(@Valid String userName) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("name").is(userName)), Update.update("role", "管理员"), "user");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    /**
     * 取消管理员权限
     *
     * @param userName 用户名
     * @return 处理反馈
     */
    @Override
    public ResponseEntity cancelUserAdminRole(@Valid String userName) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("name").is(userName)), Update.update("role", "普通用户"), "user");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    @Override
    public ResponseEntity saveSearchMethod(SearchMethod searchMethod) {
        User user = LoginChecker.checkInfo();
        if (user != null) {
            searchMethod.setOwner(user.getName());
            mongoTemplate.insert(searchMethod, "search_method");
            return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.FORBIDDEN);
    }

    @Override
    public List listSearchMethod(String type) {
        return mongoTemplate.find(
                new Query(Criteria.where("type").is(type)), Map.class, "search_method");
    }

    @Override
    public ResponseEntity delSearchMethod(String type, String name, String owner) {
        mongoTemplate.remove(
                Query.query(Criteria.where("type").is(type).and("name").is(name).and("owner").is(owner)),
                "search_method");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    /**
     * 上传计划任务
     *
     * @param item 计划任务项目
     * @return 上传结果
     */
    @Override
    public ResponseEntity postUploadSchedule(ScheduleItem item) {
        User user = LoginChecker.checkInfo();
        if (user == null) {
            return new ResponseEntity<>(new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.FORBIDDEN);
        }
        item.setOwner(user.getName());
        mongoTemplate.insert(item, "crawl_schedule");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    /**
     * 获得自定义计划任务列表
     *
     * @return 自定义计划任务列表
     */
    @Override
    public List listUploadSchedule() {
        return mongoTemplate.findAll(ScheduleItem.class, "crawl_schedule");
    }

    /**
     * 删除自定义计划任务
     *
     * @param type  自定义计划任务类型
     * @param name  自定义计划任务拥有者
     * @param owner 自定义计划任务
     * @return 删除结果
     */
    @Override
    public ResponseEntity deleteCustomSchedule(String type, String name, String owner) {
        mongoTemplate.findAndRemove(
                Query.query(Criteria.where("type").is(type).and("name").is(name).and("owner").is(owner)),
                ScheduleItem.class,
                "crawl_schedule");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }

    /**
     * 提交作者爬虫列表
     *
     * @param authorListData 作者列表、上传者、爬取频率等信息
     * @return 提交反馈
     */
    @Override
    public ResponseEntity postAuthorCrawlList(Map authorListData) {

        Date publishDate = Calendar.getInstance().getTime();
        String owner = (String) authorListData.get("owner");
        ArrayList authorList = (ArrayList) authorListData.get("authorList");
        String frequency = (String) authorListData.get("frequency");
        Map<String, Object> result = new HashMap<>(4);
        result.put("owner", owner);
        result.put("authorList", authorList);
        result.put("frequency", frequency);
        result.put("publishDate", publishDate);
        mongoTemplate.insert(result, "author_crawl_list");
        return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
    }


}
