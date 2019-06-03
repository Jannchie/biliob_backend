package com.jannchie.biliob.service;

import com.jannchie.biliob.model.ScheduleItem;
import com.jannchie.biliob.model.SearchMethod;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
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
   * @param pagesize pageszie
   * @param sort     sort
   * @param text     text
   * @param day      @return user list
   */
  List listUser(Integer page, Integer pagesize, Integer sort, String text, Integer day);

  /**
   * aggregate user
   *
   * @param ops operations
   * @return list of result
   */
  List aggregateUser(List<Map<String, Object>> ops);

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
   * @param authorListData 作者列表、上传者、爬取频率等信息
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
}
