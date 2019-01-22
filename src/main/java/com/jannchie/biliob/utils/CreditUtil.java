package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Component
public class CreditUtil {
  private static final Logger logger = LogManager.getLogger(CreditUtil.class);
  private final MongoOperations mongoTemplate;

  @Autowired
  public CreditUtil(MongoOperations mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * return user's new credit
   *
   * @param user           user information.
   * @param creditConstant the operation value.
   * @return -1: user's credit not enough || positive integer: the credit after calculate.
   */
  public HashMap<String, Integer> calculateCredit(User user, CreditConstant creditConstant) {

    Integer value = creditConstant.getValue();
    Integer credit = user.getCredit() + value;
    Integer exp = user.getExp() + Math.abs(value);
    String userName = user.getName();

    if (value < 0 && user.getCredit() < (-value)) {
      logger.info("用户：{},积分不足,当前积分：{}", userName, user.getCredit());
      HashMap<String, Integer> data = new HashMap<>(2);
      data.put("exp",exp);
      data.put("credit", -1);
      return data;
    }

    Query query = new Query(where("name").is(userName));
    Update update = new Update();
    update.set("credit", credit);
    update.set("exp", exp);
    mongoTemplate.updateFirst(query, update, User.class);
    HashMap<String, Integer> data = new HashMap<>(2);
    data.put("exp",exp);
    data.put("credit", credit);
    logger.info("用户：{},积分变动,当前积分：{}", userName, credit);
    return data;
  }
}
