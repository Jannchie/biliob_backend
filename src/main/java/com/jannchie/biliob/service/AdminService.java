package com.jannchie.biliob.service;

import com.jannchie.biliob.model.IpVisitRecord;
import com.jannchie.biliob.model.ScheduleItem;
import com.jannchie.biliob.model.SearchMethod;
import com.jannchie.biliob.object.AuthorIntervalCount;
import com.jannchie.biliob.utils.Result;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jannchie
 */
public interface AdminService {
    /**
     * list User
     *
     * @param page     page
     * @param pagesize page size
     * @param sort     sort
     * @param text     text
     * @param day      @return user list
     */
    List listUser(Integer page, Integer pagesize, Integer sort, String text, Integer day);

    /**
     * list Ip record
     *
     * @param page     page
     * @param pagesize page size
     * @param groupBy  group by
     * @param text     text
     * @param day      @return user list
     * @param regex    regex
     * @param ip       ip
     */
    List listIpRecord(Integer page, Integer pagesize, String groupBy, String text, Integer day, String regex, String ip);

    /**
     * aggregate user
     *
     * @param ops operations
     * @return list of result
     */
    List aggregateUser(List<Map<String, Object>> ops);

    /**
     * 一个聚合统计用户的方法
     *
     * @param page           页数
     * @param pagesize       页大小
     * @param day            天数
     * @param matchField     字段
     * @param matchMethod    方法
     * @param matchValue     值
     * @param sort           排序
     * @param orderBy        按照什么排序
     * @param bucketType     分桶类型
     * @param bucket         分桶
     * @param groupByField   分组
     * @param groupReference 分组依赖
     * @param groupKeyword   分组关键词
     * @return 返回列表
     */
    List aggregateUser(
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
            String groupKeyword);

    /**
     * 授予管理员权限
     *
     * @param userName 用户名
     * @return 反馈
     */
    ResponseEntity grantUserAdminRole(@Valid String userName);

    /**
     * 提交作者爬虫列表
     *
     * @param authorListData UP主名单、上传者、爬取频率等信息
     * @return 提交反馈
     */
    ResponseEntity postAuthorCrawlList(Map authorListData);

    /**
     * 保存搜索方案
     *
     * @param searchMethod 搜索方案项目
     * @return 保存结果
     */
    ResponseEntity saveSearchMethod(SearchMethod searchMethod);

    List listSearchMethod(String type);

    ResponseEntity delSearchMethod(String type, String name, String owner);

    /**
     * 上传计划任务
     *
     * @param item 计划任务项目
     * @return 上传结果
     */
    ResponseEntity postUploadSchedule(ScheduleItem item);

    /**
     * 获得自定义计划任务列表
     *
     * @return 自定义计划任务列表
     */
    List listUploadSchedule();

    /**
     * 删除自定义计划任务
     *
     * @param type  自定义计划任务类型
     * @param name  自定义计划任务拥有者
     * @param owner 自定义计划任务
     * @return 删除结果
     */
    ResponseEntity deleteCustomSchedule(String type, String name, String owner);

    /**
     * 取消管理员权限
     *
     * @param userName 用户名
     * @return 处理反馈
     */
    ResponseEntity cancelUserAdminRole(@Valid String userName);

    /**
     * 提交新的禁用UA
     *
     * @param userAgent UA
     * @return 禁用結果
     */
    ResponseEntity<Result> banUserAgent(String userAgent);

    ArrayList<IpVisitRecord> getVisitVariance();

    Double getVariance(String ip);

    Map<Integer, Integer> getDistribute(String ip);

    Result banIp(String ip);

    List<AuthorIntervalCount> getSpiderStat();
}
