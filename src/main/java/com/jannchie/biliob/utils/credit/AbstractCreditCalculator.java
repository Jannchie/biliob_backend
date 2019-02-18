package com.jannchie.biliob.utils.credit;

import com.jannchie.biliob.constant.CreditConstant;
import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.User;
import com.jannchie.biliob.model.UserRecord;
import com.jannchie.biliob.repository.UserRepository;
import com.jannchie.biliob.utils.LoginCheck;
import com.jannchie.biliob.utils.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author jannchie
 */
@Component
public abstract class AbstractCreditCalculator {
  private static final Logger logger = LogManager.getLogger(AbstractCreditCalculator.class);

  private final UserRepository userRepository;
  private final MongoOperations mongoTemplate;

  @Autowired
  public AbstractCreditCalculator(
      MongoOperations mongoTemplate, UserRepository userRepository) {
    this.userRepository = userRepository;
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * return user's new credit
   *
   * @param creditConstant the operation value.
   * @return The response of user's request.
   */
  public ResponseEntity executeAndGetResponse(CreditConstant creditConstant, Object d) {
    User user = LoginCheck.checkInfo();
    if (user == null) {
      return new ResponseEntity<>(
          new Result(ResultEnum.HAS_NOT_LOGGED_IN), HttpStatus.UNAUTHORIZED);
    }
    String userName = user.getName();


    Integer value = creditConstant.getValue();
    Integer credit = user.getCredit() + value;
    Integer exp = user.getExp() + Math.abs(value);
    HashMap<String, Integer> data;
    if (value < 0 && user.getCredit() < (-value)) {
      logger.info("用户：{},积分不足,当前积分：{}", userName, user.getCredit());
      return new ResponseEntity<>(new Result(ResultEnum.CREDIT_NOT_ENOUGH), HttpStatus.ACCEPTED);
    }

    execute(d);

    // update user info
    Query query = new Query(where("name").is(userName));
    Update update = new Update();
    update.set("credit", credit);
    update.set("exp", exp);
    update.addToSet("record", new UserRecord(new Date(), creditConstant.getMsg(), creditConstant.getValue()));
    mongoTemplate.updateFirst(query, update, User.class);

    // log
    logger.info(
        "用户：{} 积分变动:{} 原因:{}", user.getName(), creditConstant.getValue(), creditConstant.getMsg());

    data = new HashMap<>(2);
    data.put("exp", exp);
    data.put("credit", credit);

    return new ResponseEntity<>(new Result(ResultEnum.SUCCEED, data), HttpStatus.OK);

  }

  /**
   * Execute the service
   *
   * @param data just param
   */
  abstract void execute(Object data);

}
